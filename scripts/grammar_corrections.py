#!/usr/bin/env python3
"""Correct booklet typos in question text.

The official PDFs contain these mistakes. The app stores the corrected text.
sync_official_exam.py applies the same corrections on import, and
verify_questions.py compares the database with the corrected PDF text, so a
later sync does not restore the typos.

Idempotent: correcting an already corrected string changes nothing.
"""

from __future__ import annotations

import json
import re
import sqlite3
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DB_PATH = ROOT / "database/src/commonMain/resources/license_test_questions.db"

# Longer phrases first so a later rule cannot re-match the output.
REPLACEMENTS: list[tuple[str, str]] = [
    (
        "որ մինչև 100 մետր գծանցից հեռավորությունը կավարտի մանևրի կատարումը",
        "որ մանևրը կավարտի գծանցից մինչև 100 մետր հեռավորության վրա",
    ),
    (
        "որ՞ն իր ազդեցությունը չի տարածում",
        "որի՞ ազդեցությունը չի տարածվում",
    ),
    ("համաձայն թույլատրվում է", "համաձայն՝ թույլատրվու՞մ է"),
    ("համաձայն՝ երբ է", "համաձայն՝ ե՞րբ է"),
    ("դեպքում ինչ ", "դեպքում ի՞նչ "),
    ("գծանցներից ինչ ", "գծանցներից ի՞նչ "),
    ("ավելի քանի ", "ավելի քան "),
    ("թողնել տրանսպորտային միջոցը", "թողնել տրանսպորտային միջոցը։"),
    ("պահանջները\"", "պահանջները։"),
    ("արյուն:Վերքի", "արյուն։ Վերքի"),
    (',,A"', "«A»"),
    ("Թույլատրվու,մ", "Թույլատրվու՞մ"),
    ("Ի՞նչպես", "Ինչպե՞ս"),
    ("Ո՞րտեղից", "Որտեղի՞ց"),
    ("տրաանսպորտային", "տրանսպորտային"),
    ("համապատասախան", "համապատասխան"),
    ("հերթականությանբ", "հերթականությամբ"),
    ("երկաթուղայհն", "երկաթուղային"),
    ("հեռավարության", "հեռավորության"),
    ("օրեսդրությամբ", "օրենսդրությամբ"),
    ("հանդիպահաց", "հանդիպակաց"),
    ("նշաններիի", "նշանների"),
    ("գոտինելով", "գոտիներով"),
    ("աբտոբուս", "ավտոբուս"),
    ("ավտոմբիլ", "ավտոմոբիլ"),
    ("երթևեկեղ", "երթևեկող"),
    ("նախտեսված", "նախատեսված"),
    ("քարշարկ", "քարշակ"),
    ("խաղանցք", "խաղացք"),
    ("վարոդ", "վարորդ"),
    ("Թույլատվում", "Թույլատրվում"),
    ("չի տարածում", "չի տարածվում"),
    ("միջոցն կանխամտածված", "միջոցը կանխամտածված"),
    ("շարժումն սկսելու", "շարժումը սկսելու"),
    ("մանեւրն սկսելուց", "մանեւրը սկսելուց"),
    ("Մանեւրն սկսելուց", "Մանեւրը սկսելուց"),
    ("տարածքն սկսելուց", "տարածքը սկսելուց"),
    ("վարորդն համընթաց", "վարորդը համընթաց"),
    ("տարածությունն հավասար", "տարածությունը հավասար"),
    ("վարորդն պետք", "վարորդը պետք"),
    ("ժ -ով", "ժ-ով"),
    ("0\x0025", "0,25"),
]

# "միջոցը։" already ends with ։; replacing the bare phrase again would add another.
_ALREADY_STOPPED = "թողնել տրանսպորտային միջոցը։"


def correct_text(text: str) -> str:
    if not text:
        return text
    for wrong, right in REPLACEMENTS:
        if wrong == "թողնել տրանսպորտային միջոցը" and _ALREADY_STOPPED in text:
            continue
        text = text.replace(wrong, right)
    text = re.sub(r"(?<=[Ա-Ֆա-և])u(?=[Ա-Ֆա-և])", "ս", text)
    text = re.sub(r",(?=[Ա-Ֆա-և])", ", ", text)
    text = re.sub(r"[ \t]+,", ",", text)
    text = re.sub(r"[ \t]+։", "։", text)
    text = re.sub(r"(\d)մետր", r"\1 մետր", text)
    text = re.sub(r"(\d)ից", r"\1-ից", text)
    text = re.sub(r"և(\d)", r"և \1", text)
    return text


def answers_json(answers: list[str]) -> str:
    return json.dumps(answers, ensure_ascii=False, separators=(",", ":"))


def apply_to_db(db_path: Path = DB_PATH, write: bool = False) -> int:
    conn = sqlite3.connect(db_path)
    rows = conn.execute(
        "SELECT id, question, answers, true_answer FROM Question ORDER BY id"
    ).fetchall()
    changed = 0
    for qid, question, answers_raw, true_answer in rows:
        answers = json.loads(answers_raw)
        new_q = correct_text(question)
        new_answers = [correct_text(a) for a in answers]
        new_true = correct_text(true_answer)
        if new_true not in new_answers:
            raise SystemExit(f"question {qid}: corrected true answer is not in the answers")
        if new_q == question and new_answers == answers and new_true == true_answer:
            continue
        changed += 1
        print(f"#{qid}")
        if new_q != question:
            print(f"  Q: {question}")
            print(f"  → {new_q}")
        for old, new in zip(answers, new_answers):
            if old != new:
                print(f"  A: {old}")
                print(f"  → {new}")
        if write:
            conn.execute(
                "UPDATE Question SET question=?, answers=?, true_answer=? WHERE id=?",
                (new_q, answers_json(new_answers), new_true, qid),
            )
    if write:
        conn.commit()
    conn.close()
    print(f"{'updated' if write else 'would update'} {changed} questions")
    return changed


def main() -> int:
    write = "--apply" in sys.argv
    apply_to_db(write=write)
    return 0


if __name__ == "__main__":
    sys.exit(main())
