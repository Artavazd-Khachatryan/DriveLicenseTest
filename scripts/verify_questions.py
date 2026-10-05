#!/usr/bin/env python3
"""Verify DB questions against the official ABC + DT PDFs.

Source of truth is roadpolice.am (cached by scripts/official_exam.py). A question
in both papers must be exam_group=GENERAL; paper-exclusive rows are ABC or DT.
Diagram twins that share wording stay separate rows, keyed by
(book_id, printed_number, exam_group).

Usage:
    python3 scripts/verify_questions.py
    python3 scripts/verify_questions.py --cache /tmp/official-exam-2026

Exits nonzero if any official question is missing, extra, or has the wrong
correct answer / exam_group / booklet location / wording.
"""
from __future__ import annotations

import argparse
import json
import sqlite3
import sys
from pathlib import Path

from grammar_corrections import correct_text
from official_exam import (
    DEFAULT_CACHE,
    REPO,
    canon,
    content_key,
    exam_group_for,
    fetch_pdfs,
    parse_all,
    unique_official,
)

DB_PATH = REPO / "database/src/commonMain/resources/license_test_questions.db"


def load_db():
    con = sqlite3.connect(str(DB_PATH))
    con.row_factory = sqlite3.Row
    bad = con.execute(
        "SELECT id, exam_group FROM Question WHERE exam_group NOT IN ('GENERAL','ABC','DT')"
    ).fetchall()
    if bad:
        sys.exit(
            "FAIL: invalid exam_group values: "
            + ", ".join(f"q{r[0]}={r[1]!r}" for r in bad)
        )
    counts = dict(
        con.execute("SELECT exam_group, COUNT(*) FROM Question GROUP BY exam_group").fetchall()
    )
    print(f"exam_group OK: {counts}")
    rows = []
    for r in con.execute(
        "SELECT id, book_id, printed_number, question, answers, true_answer, exam_group "
        "FROM Question ORDER BY id"
    ):
        try:
            answers = json.loads(r["answers"])
        except Exception:
            answers = [r["answers"]]
        rows.append(dict(r) | {"answers": answers})
    con.close()
    return rows


def slot(row_or_rec, group=None, rec=False):
    if rec:
        return (row_or_rec.book, row_or_rec.number, exam_group_for(row_or_rec))
    return (row_or_rec["book_id"], row_or_rec["printed_number"], row_or_rec["exam_group"])


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--cache", type=Path, default=DEFAULT_CACHE)
    ap.add_argument("--refresh-pdfs", action="store_true")
    args = ap.parse_args()

    fetch_pdfs(args.cache, refresh=args.refresh_pdfs)
    unique = unique_official(parse_all(args.cache))
    db = load_db()

    db_by_slot = {}
    extra_dup = []
    for row in db:
        k = slot(row)
        if k in db_by_slot:
            extra_dup.append({"db_id": row["id"], "slot": k})
        else:
            db_by_slot[k] = row

    missing, extra, wrong_answer, wrong_text, wrong_group = [], [], [], [], []

    seen = set()
    for rec in unique:
        k = slot(rec, rec=True)
        seen.add(k)
        row = db_by_slot.get(k)
        if row is None:
            missing.append(
                {
                    "book": rec.book,
                    "number": rec.number,
                    "group": exam_group_for(rec),
                    "question": rec.question,
                }
            )
            continue
        # Booklet typos are corrected in the DB. Compare against that text.
        off_q = correct_text(rec.question)
        off_answers = [correct_text(a) for a in rec.answers]
        off_correct = correct_text(rec.correct_text)
        if content_key(row["question"], row["answers"]) != content_key(
            off_q, off_answers
        ):
            wrong_text.append(
                {
                    "db_id": row["id"],
                    "slot": k,
                    "db_q": row["question"][:80],
                    "off_q": off_q[:80],
                }
            )
        if canon(row["true_answer"]) != canon(off_correct):
            wrong_answer.append(
                {
                    "db_id": row["id"],
                    "db_correct": row["true_answer"],
                    "book_correct": off_correct,
                    "question": rec.question[:80],
                }
            )

    extra = [
        {"db_id": row["id"], "slot": k, "question": row["question"][:80]}
        for k, row in db_by_slot.items()
        if k not in seen
    ]

    print(
        f"official unique={len(unique)} db={len(db)} | "
        f"missing={len(missing)} extra={len(extra)} dup_slots={len(extra_dup)} "
        f"wrong_answer={len(wrong_answer)} wrong_text={len(wrong_text)}"
    )
    report = {
        "missing": missing,
        "extra": extra,
        "dup_slots": extra_dup,
        "wrong_answer": wrong_answer,
        "wrong_text": wrong_text,
    }
    out = REPO / "scripts/verify_report.json"
    out.write_text(json.dumps(report, ensure_ascii=False, indent=2))
    print(f"Details -> {out}")

    if missing or extra or extra_dup or wrong_answer or wrong_text:
        print("FAIL")
        return 1
    print("OK")
    return 0


if __name__ == "__main__":
    sys.exit(main())
