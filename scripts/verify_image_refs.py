#!/usr/bin/env python3
"""Verify question image references between the bundled DB and drawables.

Run after any content change, before releasing. Checks:
1. Every Question.image value resolves to a file in the drawable directory
   (matched by basename without extension, mirroring resolveDrawableResource).
2. Every Question.image follows the 'question{id}_image.*' naming that
   resolveQuestionImage() assumes.
3. Every 'question*_image.*' drawable belongs to a question that still exists
   and has its image column set (catches leftovers from removed questions and
   questions whose image column was forgotten).

Exits nonzero if any check fails.
"""

import re
import sqlite3
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DB_PATH = ROOT / "database/src/commonMain/resources/license_test_questions.db"
DRAWABLE_DIR = ROOT / "ui/src/commonMain/composeResources/drawable"

QUESTION_IMAGE_RE = re.compile(r"^question(\d+)_image$")


def main() -> int:
    drawable_keys = {p.stem for p in DRAWABLE_DIR.iterdir() if p.is_file()}

    conn = sqlite3.connect(DB_PATH)
    rows = conn.execute(
        "SELECT id, image FROM Question WHERE image IS NOT NULL"
    ).fetchall()
    all_ids = {qid for (qid,) in conn.execute("SELECT id FROM Question")}
    conn.close()

    errors = []

    # 1 + 2: DB -> drawable
    ids_with_image = set()
    for qid, image in rows:
        key = image.rsplit(".", 1)[0]
        ids_with_image.add(qid)
        if key not in drawable_keys:
            errors.append(f"question {qid}: image '{image}' has no drawable '{key}'")
        m = QUESTION_IMAGE_RE.match(key)
        if not m:
            errors.append(
                f"question {qid}: image '{image}' does not follow question{{id}}_image.* naming"
            )
        elif int(m.group(1)) != qid:
            errors.append(f"question {qid}: image '{image}' references a different id")

    # 3: drawable -> DB
    for key in sorted(drawable_keys):
        m = QUESTION_IMAGE_RE.match(key)
        if not m:
            continue  # not a question image (e.g. color vision plates, branding)
        qid = int(m.group(1))
        if qid not in all_ids:
            errors.append(f"drawable '{key}': question {qid} no longer exists in DB")
        elif qid not in ids_with_image:
            errors.append(f"drawable '{key}': question {qid} has NULL image column")

    if errors:
        print(f"FAIL: {len(errors)} problem(s)")
        for e in errors:
            print(f"  - {e}")
        return 1

    print(
        f"OK: {len(ids_with_image)} image references consistent "
        f"({len(drawable_keys)} drawables scanned)"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
