#!/usr/bin/env python3
"""Verify app question images against illustrations embedded in official book PDFs."""

from __future__ import annotations

import argparse
import json
import sqlite3
import subprocess
import tempfile
from pathlib import Path

import cv2
import numpy as np
from scipy.optimize import linear_sum_assignment

ROOT = Path(__file__).resolve().parents[1]
DB_PATH = ROOT / "database/src/commonMain/resources/license_test_questions.db"
DRAWABLE_DIR = ROOT / "ui/src/commonMain/composeResources/drawable"
BOOKS_DIR = Path("/Users/artavazd/Documents/driver_license_test/Driver License Test App")
SCRIPTS_DIR = ROOT / "scripts"

OK_MIN_SCORE = 25
OK_MIN_GAP = 8
WARN_MIN_SCORE = 12


def question_image_path(qid: int) -> Path:
    webp = DRAWABLE_DIR / f"question{qid}_image.webp"
    if webp.exists():
        return webp
    return DRAWABLE_DIR / f"question{qid}_image.png"


def extract_pdf_images(pdf_path: Path, out_dir: Path) -> list[Path]:
    subprocess.run(
        ["pdfimages", "-png", str(pdf_path), str(out_dir / "img")],
        check=True,
        capture_output=True,
    )
    return sorted(out_dir.glob("*.png"), key=lambda p: p.name)


class OrbMatcher:
    def __init__(self) -> None:
        self._orb = cv2.ORB_create(500)
        self._bf = cv2.BFMatcher(cv2.NORM_HAMMING, crossCheck=True)
        self._cache: dict[Path, np.ndarray | None] = {}

    def descriptors(self, path: Path) -> np.ndarray | None:
        if path not in self._cache:
            gray = cv2.imread(str(path), cv2.IMREAD_GRAYSCALE)
            if gray is None:
                self._cache[path] = None
            else:
                _, desc = self._orb.detectAndCompute(gray, None)
                self._cache[path] = desc
        return self._cache[path]

    def score(self, app_path: Path, pdf_path: Path) -> int:
        app_gray = cv2.imread(str(app_path), cv2.IMREAD_GRAYSCALE)
        pdf_gray = cv2.imread(str(pdf_path), cv2.IMREAD_GRAYSCALE)
        if app_gray is None or pdf_gray is None:
            return 0
        pdf_resized = cv2.resize(pdf_gray, (app_gray.shape[1], app_gray.shape[0]))
        _, app_desc = self._orb.detectAndCompute(app_gray, None)
        _, pdf_desc = self._orb.detectAndCompute(pdf_resized, None)
        if app_desc is None or pdf_desc is None:
            return 0
        matches = self._bf.match(app_desc, pdf_desc)
        return len([m for m in matches if m.distance < 50])


def classify_match(score: int, second_best: int) -> str:
    gap = score - second_best
    if score >= OK_MIN_SCORE and gap >= OK_MIN_GAP:
        return "ok"
    if score >= WARN_MIN_SCORE:
        return "warn"
    return "mismatch"


def audit_book(book_id: int, matcher: OrbMatcher) -> dict:
    pdf_path = BOOKS_DIR / f"խումբ {book_id}.pdf"
    if not pdf_path.exists():
        raise FileNotFoundError(pdf_path)

    conn = sqlite3.connect(DB_PATH)
    qids = [
        row[0]
        for row in conn.execute(
            """
            SELECT id FROM Question
            WHERE book_id = ? AND image IS NOT NULL AND image != ''
            ORDER BY id
            """,
            (book_id,),
        )
    ]
    conn.close()

    if not qids:
        return {
            "book_id": book_id,
            "app_images": 0,
            "pdf_images": 0,
            "ok": 0,
            "warn": 0,
            "mismatch": 0,
            "missing_asset": 0,
            "issues": [],
        }

    missing_asset = [qid for qid in qids if not question_image_path(qid).exists()]

    with tempfile.TemporaryDirectory(prefix=f"audit_b{book_id}_") as tmp:
        pdf_files = extract_pdf_images(pdf_path, Path(tmp))
        app_files = [question_image_path(qid) for qid in qids]

        n, m = len(qids), len(pdf_files)
        scores = np.zeros((n, m), dtype=int)

        if n == m:
            # Fast path: images appear in book order; verify each pair + build matrix from top-k.
            for i, (app_path, pdf_path) in enumerate(zip(app_files, pdf_files, strict=True)):
                scores[i, i] = matcher.score(app_path, pdf_path)
                # Score a small neighborhood for gap / swap detection.
                for j in range(max(0, i - 2), min(m, i + 3)):
                    if j != i:
                        scores[i, j] = matcher.score(app_path, pdf_files[j])
            # Fill remaining cells only when needed for assignment fallback.
            for i in range(n):
                if scores[i, i] >= WARN_MIN_SCORE:
                    continue
                for j in range(m):
                    if scores[i, j] == 0:
                        scores[i, j] = matcher.score(app_files[i], pdf_files[j])
        else:
            for i, app_path in enumerate(app_files):
                for j, pdf_path in enumerate(pdf_files):
                    scores[i, j] = matcher.score(app_path, pdf_path)

        size = max(n, m)
        cost = np.full((size, size), 10_000, dtype=int)
        cost[:n, :m] = -scores
        _, col_ind = linear_sum_assignment(cost)

        issues: list[dict] = []
        ok = warn = mismatch = 0

        for i, qid in enumerate(qids):
            j = int(col_ind[i])
            best = int(scores[i, j]) if j < m else 0
            others = [int(scores[i, k]) for k in range(m) if k != j]
            second = max(others) if others else 0
            status = classify_match(best, second)
            if status == "ok":
                ok += 1
            elif status == "warn":
                warn += 1
            else:
                mismatch += 1
            if status != "ok":
                issues.append(
                    {
                        "id": qid,
                        "status": status,
                        "score": best,
                        "second_best": second,
                        "pdf_file": pdf_files[j].name if j < m else None,
                    }
                )

        return {
            "book_id": book_id,
            "pdf": str(pdf_path),
            "app_images": len(qids),
            "pdf_images": len(pdf_files),
            "count_delta": len(pdf_files) - len(qids),
            "ok": ok,
            "warn": warn,
            "mismatch": mismatch,
            "missing_asset": len(missing_asset),
            "missing_asset_ids": missing_asset,
            "issues": issues,
        }


def write_summary(reports: list[dict]) -> None:
    path = SCRIPTS_DIR / "IMAGE_AUDIT_SUMMARY.md"
    lines = [
        "# Image audit vs official book PDFs",
        "",
        "Method: ORB feature matching between app PNGs and images extracted from PDFs (`pdfimages`).",
        "",
        "| Book | App | PDF | OK | Warn | Mismatch | Missing asset |",
        "|------|-----|-----|-----|------|----------|---------------|",
    ]
    for r in reports:
        lines.append(
            f"| {r['book_id']} | {r['app_images']} | {r['pdf_images']} | "
            f"{r['ok']} | {r['warn']} | {r['mismatch']} | {r['missing_asset']} |"
        )

    total_app = sum(r["app_images"] for r in reports)
    total_ok = sum(r["ok"] for r in reports)
    total_warn = sum(r["warn"] for r in reports)
    total_mm = sum(r["mismatch"] for r in reports)
    lines.extend(
        [
            "",
            f"**Total:** {total_ok}/{total_app} OK, {total_warn} warn, {total_mm} mismatch",
            "",
            "## Needs review (warn + mismatch)",
            "",
        ]
    )

    for r in reports:
        flagged = [i for i in r["issues"] if i["status"] in ("warn", "mismatch")]
        if not flagged:
            continue
        lines.append(f"### Book {r['book_id']}")
        for item in flagged:
            lines.append(
                f"- **{item['id']}** ({item['status']}): score={item['score']}, "
                f"2nd={item['second_best']}, pdf={item['pdf_file']}"
            )
        lines.append("")

    path.write_text("\n".join(lines), encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser(description="Audit question images against book PDFs")
    parser.add_argument("book", nargs="?", type=int, help="Book number 1-10")
    parser.add_argument("--all", action="store_true", help="Audit all books")
    args = parser.parse_args()

    books = range(1, 11) if args.all else ([args.book] if args.book else [1])

    matcher = OrbMatcher()
    reports: list[dict] = []

    for book_id in books:
        report = audit_book(book_id, matcher)
        reports.append(report)
        out = SCRIPTS_DIR / f"audit_images_{book_id}.json"
        out.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
        print(
            f"Book {book_id}: {report['ok']} OK, {report['warn']} warn, "
            f"{report['mismatch']} mismatch (of {report['app_images']} images)",
            flush=True,
        )

    if args.all:
        combined = SCRIPTS_DIR / "audit_images_all.json"
        combined.write_text(json.dumps(reports, ensure_ascii=False, indent=2), encoding="utf-8")
        write_summary(reports)
        print(f"\nSummary: {SCRIPTS_DIR / 'IMAGE_AUDIT_SUMMARY.md'}", flush=True)


if __name__ == "__main__":
    main()
