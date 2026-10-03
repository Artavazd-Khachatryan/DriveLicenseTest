#!/usr/bin/env python3
"""Sync the bundled question DB from the official ABC + DT PDFs.

Repeatable:
    python3 scripts/sync_official_exam.py              # fetch + parse + dry-run
    python3 scripts/sync_official_exam.py --apply      # write DB + images
    python3 scripts/verify_questions.py
    python3 scripts/verify_image_refs.py
    python3 scripts/set_content_version.py <n+1>
    # then set ContentRefresh.CONTENT_VERSION to the same number

Matching (stable ids):
  1. exact canon(question)+sorted(answers)
  2. unique canon(question) on both sides
  3. fuzzy ≥ --fuzzy (default 0.92) on remaining
Unmatched DB rows are deleted (ids never reused). New official questions get
MAX(id)+1. GENERAL = in both papers; otherwise exclusive ABC/DT.
Images are clipped from the preferred PDF (ABC if shared).
"""
from __future__ import annotations

import argparse
import json
import shutil
import sqlite3
import subprocess
import sys
import tempfile
from difflib import SequenceMatcher
from pathlib import Path

from PIL import Image

from official_exam import (
    DEFAULT_CACHE,
    REPO,
    OfficialRecord,
    canon,
    content_key,
    exam_group_for,
    fetch_pdfs,
    parse_all,
    question_key,
    unique_official,
)

DB_PATH = REPO / "database/src/commonMain/resources/license_test_questions.db"
DRAWABLE_DIR = REPO / "ui/src/commonMain/composeResources/drawable"
REPORT_PATH = REPO / "scripts/review/official-sync-report.json"
FUZZY_DEFAULT = 0.92
MIN_IMAGE_GAP = 36.0  # PDF points between question text and first answer
WEBP_QUALITY = 85


def load_db(conn: sqlite3.Connection) -> list[dict]:
    conn.row_factory = sqlite3.Row
    rows = []
    for r in conn.execute(
        "SELECT id, question, answers, true_answer, book_id, printed_number, "
        "exam_group, image FROM Question ORDER BY id"
    ):
        try:
            answers = json.loads(r["answers"])
        except Exception:
            answers = [r["answers"]]
        rows.append(
            {
                "id": r["id"],
                "question": r["question"],
                "answers": answers,
                "true_answer": r["true_answer"],
                "book_id": r["book_id"],
                "printed_number": r["printed_number"],
                "exam_group": r["exam_group"],
                "image": r["image"],
            }
        )
    return rows


def answers_json(answers: list[str]) -> str:
    return json.dumps(answers, ensure_ascii=False, separators=(",", ":"))


def fuzzy_ratio(a: str, b: str) -> float:
    if not a or not b:
        return 0.0
    return SequenceMatcher(None, a, b).ratio()


def match(db_rows: list[dict], official: list[OfficialRecord], fuzzy: float):
    db_unused = {d["id"]: d for d in db_rows}
    off_unused = {id(r): r for r in official}
    pairs: list[tuple[dict, OfficialRecord, str]] = []

    def take(did: int, rec: OfficialRecord, how: str) -> None:
        pairs.append((db_unused.pop(did), rec, how))
        off_unused.pop(id(rec), None)

    # 0. booklet slot (repeatable re-runs after a previous apply)
    db_slot = {}
    for d in db_rows:
        db_slot[(d["book_id"], d["printed_number"], d["exam_group"])] = d["id"]
    for rec in official:
        k = (rec.book, rec.number, exam_group_for(rec))
        did = db_slot.get(k)
        if did is not None and did in db_unused and id(rec) in off_unused:
            take(did, rec, "slot")

    # 1. strict content key, only when unique on both sides (diagram twins
    # share wording; pairing those by text would swap answers).
    db_ck: dict[tuple, list[int]] = {}
    for d in db_rows:
        db_ck.setdefault(content_key(d["question"], d["answers"]), []).append(d["id"])
    off_ck: dict[tuple, list[OfficialRecord]] = {}
    for rec in official:
        off_ck.setdefault(content_key(rec.question, rec.answers), []).append(rec)
    for ck, recs in off_ck.items():
        ids = [i for i in db_ck.get(ck, []) if i in db_unused]
        recs = [r for r in recs if id(r) in off_unused]
        if len(ids) == 1 and len(recs) == 1:
            take(ids[0], recs[0], "strict")

    # 2. unique question text
    db_q: dict[str, list[int]] = {}
    for did, d in db_unused.items():
        db_q.setdefault(question_key(d["question"]), []).append(did)
    off_q: dict[str, list[OfficialRecord]] = {}
    for rec in list(off_unused.values()):
        off_q.setdefault(question_key(rec.question), []).append(rec)
    for qk, recs in off_q.items():
        ids = db_q.get(qk, [])
        if len(ids) == 1 and len(recs) == 1:
            take(ids[0], recs[0], "question")

    # 3. fuzzy only for questions whose wording is unique on both remaining
    # sides — never pair generic "who yields" diagram clones by similarity.
    remaining_db = list(db_unused.values())
    remaining_off = list(off_unused.values())
    db_q_left: dict[str, list[int]] = {}
    for d in remaining_db:
        db_q_left.setdefault(question_key(d["question"]), []).append(d["id"])
    off_q_left: dict[str, list[OfficialRecord]] = {}
    for rec in remaining_off:
        off_q_left.setdefault(question_key(rec.question), []).append(rec)
    uniq_db = {d["id"] for d in remaining_db if len(db_q_left[question_key(d["question"])]) == 1}
    uniq_off = {id(r) for r in remaining_off if len(off_q_left[question_key(r.question)]) == 1}
    scored: list[tuple[float, int, OfficialRecord]] = []
    for d in remaining_db:
        if d["id"] not in uniq_db:
            continue
        da = canon(d["question"]) + "|" + "|".join(canon(a) for a in d["answers"])
        for rec in remaining_off:
            if id(rec) not in uniq_off:
                continue
            oa = canon(rec.question) + "|" + "|".join(canon(a) for a in rec.answers)
            r = fuzzy_ratio(da, oa)
            if r >= fuzzy:
                scored.append((r, d["id"], rec))
    scored.sort(reverse=True)
    used_db, used_off = set(), set()
    for r, did, rec in scored:
        if did in used_db or id(rec) in used_off:
            continue
        if did not in db_unused or id(rec) not in off_unused:
            continue
        take(did, rec, f"fuzzy:{r:.3f}")
        used_db.add(did)
        used_off.add(id(rec))

    return pairs, list(db_unused.values()), list(off_unused.values())


_PDF_DOCS = {}


def _pdf_doc(path: Path):
    import fitz
    doc = _PDF_DOCS.get(str(path))
    if doc is None:
        doc = fitz.open(str(path))
        _PDF_DOCS[str(path)] = doc
    return doc


def clip_image(rec: OfficialRecord) -> Path | None:
    """Render the diagram between question and answers; None if no diagram."""
    if rec.page_index is None or rec.q_bottom is None or rec.ans_top is None:
        return None
    gap = rec.ans_top - rec.q_bottom
    if gap < MIN_IMAGE_GAP and not rec.image_bboxes:
        return None
    import fitz
    doc = _pdf_doc(rec.pdf_path)
    page = doc[rec.page_index]
    if rec.image_bboxes:
        x0 = min(b[0] for b in rec.image_bboxes) - 4
        y0 = min(b[1] for b in rec.image_bboxes) - 4
        x1 = max(b[2] for b in rec.image_bboxes) + 4
        y1 = max(b[3] for b in rec.image_bboxes) + 4
    else:
        x0, x1 = 40.0, page.rect.width - 40.0
        y0, y1 = rec.q_bottom + 2, rec.ans_top - 2
    clip = fitz.Rect(max(0, x0), max(0, y0), min(page.rect.width, x1), min(page.rect.height, y1))
    if clip.height < 20 or clip.width < 20:
        return None
    pix = page.get_pixmap(matrix=fitz.Matrix(2, 2), clip=clip, alpha=False)
    tmp = Path(tempfile.mkstemp(suffix=".png")[1])
    pix.save(str(tmp))
    # trim near-white borders; drop if the clip is essentially blank
    im = Image.open(tmp).convert("RGB")
    w, h = im.size
    px = im.load()
    def is_ink(x, y):
        r, g, b = px[x, y]
        return r < 245 or g < 245 or b < 245
    top = next((y for y in range(h) if any(is_ink(x, y) for x in range(0, w, 3))), None)
    if top is None:
        tmp.unlink(missing_ok=True)
        return None
    bot = next((y for y in range(h - 1, -1, -1) if any(is_ink(x, y) for x in range(0, w, 3))))
    left = next((x for x in range(w) if any(is_ink(x, y) for y in range(0, h, 3))))
    right = next((x for x in range(w - 1, -1, -1) if any(is_ink(x, y) for y in range(0, h, 3))))
    pad = 8
    im = im.crop(
        (
            max(0, left - pad),
            max(0, top - pad),
            min(w, right + 1 + pad),
            min(h, bot + 1 + pad),
        )
    )
    im.save(tmp, "PNG")
    return tmp


def to_webp(png_path: Path, dest: Path) -> None:
    dest.parent.mkdir(parents=True, exist_ok=True)
    proc = subprocess.run(
        ["cwebp", "-q", str(WEBP_QUALITY), "-m", "6", str(png_path), "-o", str(dest)],
        capture_output=True,
        text=True,
    )
    if proc.returncode != 0:
        # pillow fallback
        Image.open(png_path).save(dest, "WEBP", quality=WEBP_QUALITY, method=6)


def remove_question_drawables(qid: int) -> None:
    for p in DRAWABLE_DIR.glob(f"question{qid}_image.*"):
        p.unlink()


def apply(
    conn: sqlite3.Connection,
    pairs: list[tuple[dict, OfficialRecord, str]],
    db_gone: list[dict],
    off_new: list[OfficialRecord],
    extract_images: bool,
) -> dict:
    stats = {
        "updated": 0,
        "inserted": 0,
        "deleted": 0,
        "images_written": 0,
        "images_cleared": 0,
    }
    max_id = conn.execute("SELECT COALESCE(MAX(id), 0) FROM Question").fetchone()[0]

    def write_image(qid: int, rec: OfficialRecord) -> str | None:
        if not extract_images:
            return None
        png = clip_image(rec)
        if png is None:
            remove_question_drawables(qid)
            stats["images_cleared"] += 1
            return None
        dest = DRAWABLE_DIR / f"question{qid}_image.webp"
        to_webp(png, dest)
        png.unlink(missing_ok=True)
        # drop leftover png/jpeg with the same stem
        for p in DRAWABLE_DIR.glob(f"question{qid}_image.*"):
            if p != dest:
                p.unlink()
        stats["images_written"] += 1
        return dest.name

    conn.execute("BEGIN")
    try:
        for dbq, rec, _how in pairs:
            image_name = dbq["image"]
            if extract_images:
                image_name = write_image(dbq["id"], rec)
            conn.execute(
                "UPDATE Question SET question=?, answers=?, true_answer=?, "
                "book_id=?, printed_number=?, exam_group=?, image=? WHERE id=?",
                (
                    rec.question,
                    answers_json(rec.answers),
                    rec.correct_text,
                    rec.book,
                    rec.number,
                    exam_group_for(rec),
                    image_name,
                    dbq["id"],
                ),
            )
            stats["updated"] += 1
            if extract_images and stats["updated"] % 50 == 0:
                print(f"  updated {stats['updated']}/{len(pairs)} images={stats['images_written']}")

        for dbq in db_gone:
            conn.execute("DELETE FROM Question WHERE id=?", (dbq["id"],))
            remove_question_drawables(dbq["id"])
            stats["deleted"] += 1

        next_id = max_id + 1
        for rec in off_new:
            qid = next_id
            next_id += 1
            image_name = write_image(qid, rec) if extract_images else None
            conn.execute(
                "INSERT INTO Question (id, question, image, answers, true_answer, "
                "book_id, printed_number, exam_group) VALUES (?,?,?,?,?,?,?,?)",
                (
                    qid,
                    rec.question,
                    image_name,
                    answers_json(rec.answers),
                    rec.correct_text,
                    rec.book,
                    rec.number,
                    exam_group_for(rec),
                ),
            )
            stats["inserted"] += 1

        conn.execute(
            "DELETE FROM QuestionCategoryJunction WHERE question_id NOT IN (SELECT id FROM Question)"
        )

        # user tables in the bundled file must stay empty
        for table in (
            "UserQuestionProgress",
            "TestSession",
            "QuestionAttempt",
            "UserStatistics",
        ):
            n = conn.execute(f"SELECT COUNT(*) FROM {table}").fetchone()[0]
            if n:
                raise RuntimeError(f"bundled DB {table} is not empty ({n} rows)")

        conn.commit()
        # drop leftover question images for ids that no longer exist
        import re as _re
        ids = {row[0] for row in conn.execute("SELECT id FROM Question")}
        orphan = 0
        for img in DRAWABLE_DIR.glob("question*_image.*"):
            m = _re.match(r"question(\d+)_image$", img.stem)
            if m and int(m.group(1)) not in ids:
                img.unlink()
                orphan += 1
        stats["orphan_drawables_removed"] = orphan
    except Exception:
        conn.rollback()
        raise
    return stats


def summarize(pairs, db_gone, off_new) -> dict:
    group_changes = {}
    true_changes = text_changes = ans_changes = 0
    for dbq, rec, how in pairs:
        ng = exam_group_for(rec)
        if dbq["exam_group"] != ng:
            key = f"{dbq['exam_group']}→{ng}"
            group_changes[key] = group_changes.get(key, 0) + 1
        if canon(dbq["true_answer"]) != canon(rec.correct_text):
            true_changes += 1
        if canon(dbq["question"]) != canon(rec.question):
            text_changes += 1
        if content_key(dbq["question"], dbq["answers"]) != content_key(
            rec.question, rec.answers
        ):
            ans_changes += 1
    how_counts: dict[str, int] = {}
    for _, _, how in pairs:
        bucket = how.split(":")[0]
        how_counts[bucket] = how_counts.get(bucket, 0) + 1
    return {
        "matched": len(pairs),
        "match_how": how_counts,
        "unmatched_db": len(db_gone),
        "new_official": len(off_new),
        "group_changes": group_changes,
        "true_answer_changes": true_changes,
        "text_changes": text_changes,
        "answer_set_changes": ans_changes,
        "exam_group_new": _group_tally(pairs, off_new),
    }


def _group_tally(pairs, off_new) -> dict[str, int]:
    t: dict[str, int] = {}
    for _, rec, _ in pairs:
        g = exam_group_for(rec)
        t[g] = t.get(g, 0) + 1
    for rec in off_new:
        g = exam_group_for(rec)
        t[g] = t.get(g, 0) + 1
    return t


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--apply", action="store_true", help="write DB and images")
    ap.add_argument("--skip-images", action="store_true")
    ap.add_argument("--refresh-pdfs", action="store_true")
    ap.add_argument("--cache", type=Path, default=DEFAULT_CACHE)
    ap.add_argument("--fuzzy", type=float, default=FUZZY_DEFAULT)
    args = ap.parse_args()

    fetch_pdfs(args.cache, refresh=args.refresh_pdfs)
    records = parse_all(args.cache)
    unique = unique_official(records)
    print(f"cells={len(records)} unique={len(unique)}")

    conn = sqlite3.connect(str(DB_PATH))
    db_rows = load_db(conn)
    pairs, db_gone, off_new = match(db_rows, unique, args.fuzzy)
    summary = summarize(pairs, db_gone, off_new)
    print(json.dumps(summary, ensure_ascii=False, indent=2))
    REPORT_PATH.parent.mkdir(parents=True, exist_ok=True)
    REPORT_PATH.write_text(
        json.dumps(
            {
                **summary,
                "gone_ids": [d["id"] for d in db_gone],
                "gone_preview": [
                    {"id": d["id"], "q": d["question"][:80]} for d in db_gone[:30]
                ],
                "new_preview": [
                    {
                        "paper": exam_group_for(r),
                        "book": r.book,
                        "n": r.number,
                        "q": r.question[:80],
                    }
                    for r in off_new[:30]
                ],
            },
            ensure_ascii=False,
            indent=2,
        )
    )
    print(f"report -> {REPORT_PATH}")

    if not args.apply:
        print("dry-run (pass --apply to write)")
        conn.close()
        return 0

    backup = args.cache / "license_test_questions.db.bak"
    shutil.copy2(DB_PATH, backup)
    print(f"backup -> {backup}")
    stats = apply(conn, pairs, db_gone, off_new, extract_images=not args.skip_images)
    print("apply", json.dumps(stats))
    conn.execute("VACUUM")
    conn.close()
    return 0


if __name__ == "__main__":
    sys.exit(main())
