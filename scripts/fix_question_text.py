#!/usr/bin/env python3
"""Normalize question and answer text in the bundled SQLite database."""

from __future__ import annotations

import json
import re
import sqlite3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DB_PATH = ROOT / "database/src/commonMain/resources/license_test_questions.db"

ARMENIAN_COMMA = "\u055d"


def normalize(text: str) -> str:
    if not text or not text.strip():
        return text
    text = text.replace("::", ":")
    text = re.sub(r"։{2,}", "։", text)
    text = text.replace("`", ARMENIAN_COMMA)
    text = re.sub(r"[\r\n]+", " ", text)
    text = re.sub(r"[ \t]+", " ", text)
    return text.strip()


def main() -> None:
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT id, question, answers, true_answer FROM Question")
    rows = cur.fetchall()
    updated = 0
    mismatches_after = []
    for qid, question, answers_json, true_answer in rows:
        new_question = normalize(question)
        answers = json.loads(answers_json)
        new_answers = [normalize(a) for a in answers]
        new_true = normalize(true_answer)
        if new_true not in new_answers:
            mismatches_after.append(qid)
        if new_question != question or new_answers != answers or new_true != true_answer:
            cur.execute(
                "UPDATE Question SET question = ?, answers = ?, true_answer = ? WHERE id = ?",
                (new_question, json.dumps(new_answers, ensure_ascii=False), new_true, qid),
            )
            updated += 1
    conn.commit()
    conn.close()
    print(f"Updated {updated} of {len(rows)} questions in {DB_PATH}")
    if mismatches_after:
        raise SystemExit(f"ERROR: {len(mismatches_after)} questions have true_answer not in answers: {mismatches_after[:10]}")
    print("Validation OK: every true_answer is present in its answers list.")


if __name__ == "__main__":
    main()
