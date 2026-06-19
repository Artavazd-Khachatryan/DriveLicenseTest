#!/usr/bin/env python3
"""Compare app questions in the DB against official book PDFs (pdftotext)."""

from __future__ import annotations

import argparse
import json
import re
import sqlite3
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DB_PATH = ROOT / "database/src/commonMain/resources/license_test_questions.db"
BOOKS_DIR = Path("/Users/artavazd/Documents/driver_license_test/Driver License Test App")
SCRIPTS_DIR = ROOT / "scripts"

BOOK_RANGES = {
    1: (1, 147),
    2: (148, 219),
    3: (220, 297),
    4: (298, 473),
    5: (474, 608),
    6: (609, 703),
    7: (704, 837),
    8: (838, 917),
    9: (918, 1043),
    10: (1044, 1094),
}


def normalize(text: str) -> str:
    text = text.lower()
    text = re.sub(r"[\s\u00a0]+", " ", text)
    text = re.sub(r"[«»\"'։:.!?՝,?]", "", text)
    text = text.replace("եւ", "և")
    text = text.replace("ո՞ո՞", "ո՞")
    return text.strip()


def compact(text: str) -> str:
    """Strip spaces so PDF column breaks do not break substring matches."""
    return normalize(text).replace(" ", "")


def find_in_book(question: str, norm_book: str, compact_book: str) -> str:
    q_norm = normalize(question)
    if not q_norm:
        return "missing"

    if len(q_norm) < 40:
        return "ok" if q_norm in norm_book else "missing"

    weak_candidate = False

    for size in (56, 48, 40, 32, 24):
        if len(q_norm) < size:
            continue
        if q_norm[:size] in norm_book:
            if size >= 40:
                return "ok"
            weak_candidate = True

    # Sliding substring for wrapped / punctuation differences.
    for start in range(0, max(1, len(q_norm) - 20)):
        sub = q_norm[start : start + 28]
        if len(sub) >= 18 and sub in norm_book:
            weak_candidate = True
            break

    # PDF two-column layout often inserts large gaps between words.
    q_compact = compact(question)
    if len(q_compact) >= 24:
        for size in (56, 48, 40, 32):
            if len(q_compact) < size:
                continue
            if q_compact[:size] in compact_book:
                return "ok"
        for start in range(0, max(1, len(q_compact) - 20)):
            sub = q_compact[start : start + 28]
            if len(sub) >= 18 and sub in compact_book:
                return "ok"

    if weak_candidate:
        return "weak"
    return "missing"


def extract_pdf_text(pdf_path: Path) -> str:
    result = subprocess.run(
        ["pdftotext", "-layout", str(pdf_path), "-"],
        capture_output=True,
        text=True,
        check=True,
    )
    return result.stdout


def load_questions(book_id: int) -> list[dict]:
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    rows = conn.execute(
        """
        SELECT id, question, answers, true_answer, image
        FROM Question
        WHERE book_id = ?
        ORDER BY id
        """,
        (book_id,),
    ).fetchall()
    conn.close()
    return [dict(row) for row in rows]


def audit_book(book_id: int) -> dict:
    pdf_path = BOOKS_DIR / f"խումբ {book_id}.pdf"
    if not pdf_path.exists():
        raise FileNotFoundError(pdf_path)

    norm_book = normalize(extract_pdf_text(pdf_path))
    compact_book = norm_book.replace(" ", "")
    questions = load_questions(book_id)

    ok: list[int] = []
    weak_match: list[dict] = []
    missing_text: list[dict] = []

    for q in questions:
        status = find_in_book(q["question"], norm_book, compact_book)
        if status == "ok":
            ok.append(q["id"])
        elif status == "weak":
            weak_match.append(
                {
                    "id": q["id"],
                    "reason": "partial / formatting match",
                    "question": q["question"][:100],
                }
            )
        else:
            missing_text.append(
                {
                    "id": q["id"],
                    "question": q["question"],
                    "true_answer": q["true_answer"],
                }
            )

    return {
        "book_id": book_id,
        "pdf": str(pdf_path),
        "total": len(questions),
        "ok": len(ok),
        "weak": len(weak_match),
        "missing": len(missing_text),
        "missing_text": missing_text,
        "weak_match": weak_match,
    }


def write_summary(reports: list[dict]) -> None:
    summary_path = SCRIPTS_DIR / "AUDIT_SUMMARY.md"
    lines = [
        "# Question audit vs official books",
        "",
        "| Book | OK | Weak | Missing | Total |",
        "|------|-----|------|---------|-------|",
    ]
    for r in reports:
        lines.append(
            f"| {r['book_id']} | {r['ok']} | {r['weak']} | {r['missing']} | {r['total']} |"
        )

    lines.extend(["", "## Issues requiring manual review", ""])
    for r in reports:
        if not r["missing_text"] and not r["weak_match"]:
            continue
        lines.append(f"### Book {r['book_id']}")
        for item in r["missing_text"]:
            q = item["question"].replace("\n", " ")[:90]
            lines.append(f"- **{item['id']}** (missing): {q}")
        for item in r["weak_match"]:
            q = item["question"].replace("\n", " ")[:90]
            lines.append(f"- **{item['id']}** (weak): {q}")
        lines.append("")

    summary_path.write_text("\n".join(lines), encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser(description="Audit app questions against book PDFs")
    parser.add_argument("book", nargs="?", type=int, help="Book number 1-10")
    parser.add_argument("--all", action="store_true", help="Audit all 10 books")
    args = parser.parse_args()

    if args.all:
        books = range(1, 11)
    elif args.book:
        books = [args.book]
    else:
        books = [1]

    reports: list[dict] = []
    for book_id in books:
        report = audit_book(book_id)
        reports.append(report)
        out = SCRIPTS_DIR / f"audit_book_{book_id}.json"
        out.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
        print(
            f"Book {book_id}: {report['ok']} OK, {report['weak']} weak, "
            f"{report['missing']} missing (of {report['total']})"
        )

    if args.all:
        combined = SCRIPTS_DIR / "audit_all.json"
        combined.write_text(json.dumps(reports, ensure_ascii=False, indent=2), encoding="utf-8")
        write_summary(reports)
        print(f"\nSummary: {SCRIPTS_DIR / 'AUDIT_SUMMARY.md'}")
        print(f"Combined: {combined}")


if __name__ == "__main__":
    main()
