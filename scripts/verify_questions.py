#!/usr/bin/env python3
"""Verify DB questions/answers against the source book PDFs.

Each book PDF lays questions in a 2-column grid. Every question cell ends with a
`Պատ․՝N` marker giving the correct-answer index (1-based). Footer blocks carry the
printed question number, which equals the DB `id`.

This script parses each PDF into {number -> question record} and compares against
the Question table, reporting text / answer / correct-answer mismatches.
"""
import json
import re
import sqlite3
import sys
import unicodedata
from pathlib import Path

import fitz  # PyMuPDF

REPO = Path(__file__).resolve().parents[1]
DB_PATH = REPO / "database/src/commonMain/resources/license_test_questions.db"
BOOKS_DIR = Path("/Users/artavazd/Documents/driver_license_test/Driver License Test App")

# book_id -> pdf filename
BOOK_PDF = {i: f"խումբ {i}.pdf" for i in range(1, 11)}

MID_X = 300.0  # column split
ANS_RE = re.compile(r"(?:^|\s)(\d)\.")            # answer marker "1." "2." ...
PAT_RE = re.compile(r"Պատ[^\d]*?(\d)")  # correct answer marker "Պատ․՝N"
NUM_RE = re.compile(r"^\s*(\d+)\s*$")


def norm(s: str) -> str:
    """Normalize for comparison: NFC, collapse whitespace, strip."""
    s = unicodedata.normalize("NFC", s or "")
    s = s.replace(" ", " ")
    s = re.sub(r"\s+", " ", s).strip()
    return s


def squeeze(s: str) -> str:
    """Aggressive: remove ALL whitespace, for resilient equality checks."""
    return re.sub(r"\s+", "", unicodedata.normalize("NFC", s or ""))


def canon(s: str) -> str:
    """Canonical content form: keep only alphanumerics (Armenian letters + digits).

    Drops ALL punctuation/whitespace/quotes so cosmetic differences (the book's
    backtick vs DB's ՝, trailing ։/: , «» quotes) don't count as mismatches. Only
    genuine word/number differences survive.
    """
    s = unicodedata.normalize("NFC", s or "").casefold()
    # drop Armenian punctuation/quote block (U+0559..U+055F) even though some are
    # classed as letters (e.g. ՙ U+0559), and keep only remaining alphanumerics.
    return "".join(ch for ch in s if ch.isalnum() and not (0x0559 <= ord(ch) <= 0x055F))


LABEL_RE = re.compile(r"^\s*(\d)\.\s*(.*)$", re.S)


def parse_cell(text: str):
    """Parse one question cell's raw text into (question, answers[], correct_idx).

    Answer labels ("1." "2." ...) are detected only at the START of a line, which
    distinguishes them from decimals like "3.5 տ" or numeric answers like "1.110 կմ/ժ"
    (the latter is a label "1." followed by the value "110"). Answer lines may wrap;
    continuation lines are appended to the current answer.
    """
    m = PAT_RE.search(text)
    correct = int(m.group(1)) if m else None
    if m:
        text = text[: m.start()]

    answers, q_lines, cur = [], [], None
    expect = 1
    for line in text.split("\n"):
        mm = LABEL_RE.match(line)
        if mm and int(mm.group(1)) == expect:
            if cur is not None:
                answers.append(cur)
            cur = mm.group(2)
            expect += 1
        elif cur is not None:
            cur = (cur + " " + line).strip()
        else:
            q_lines.append(line)
    if cur is not None:
        answers.append(cur)

    question = norm(" ".join(q_lines))
    answers = [norm(a) for a in answers]
    return question, answers, correct


def parse_book(pdf_path: Path):
    """Return list of {number, question, answers, correct} cells.

    `number` is the best-effort printed question number (a hint for reporting only;
    the footer numbering order is not reliable across books, so correctness checks
    are done by content, not by number).
    """
    doc = fitz.open(str(pdf_path))
    result = []
    for pg in doc:
        blocks = pg.get_text("blocks")
        left_cells, right_cells = [], []   # (y0, raw_text)
        left_nums, right_nums = [], []     # (y0, number)
        for x0, y0, x1, y1, txt, bno, btype in blocks:
            t = txt.strip()
            if not t:
                continue
            # footer number block: text is only digits + whitespace
            if re.sub(r"\s+", "", t).isdigit():
                only_nums = re.findall(r"\d+", t)
                if len(only_nums) == 2:
                    left_nums.append((y0, int(only_nums[0])))
                    right_nums.append((y0, int(only_nums[1])))
                    continue
                if len(only_nums) == 1:
                    if x0 < MID_X:
                        left_nums.append((y0, int(only_nums[0])))
                    else:
                        right_nums.append((y0, int(only_nums[0])))
                    continue
            # content block: attribute to column
            if x0 < MID_X:
                left_cells.append((y0, txt))
            else:
                right_cells.append((y0, txt))

        for cells, nums in ((left_cells, left_nums), (right_cells, right_nums)):
            cells.sort(key=lambda c: c[0])
            nums.sort(key=lambda n: n[0])
            # group content blocks into cells by Պատ marker
            grouped = []
            buf = []
            for y0, txt in cells:
                buf.append(txt)
                if PAT_RE.search(txt):
                    grouped.append("\n".join(buf))
                    buf = []
            if buf:
                grouped.append("\n".join(buf))
            # pair grouped cells with numbers in order
            for i, raw in enumerate(grouped):
                number = nums[i][1] if i < len(nums) else None
                q, a, c = parse_cell(raw)
                result.append({"number": number, "question": q, "answers": a, "correct": c})
    return result


def load_db():
    con = sqlite3.connect(str(DB_PATH))
    con.row_factory = sqlite3.Row
    rows = con.execute(
        "SELECT id, book_id, question, answers, true_answer, image FROM Question ORDER BY id"
    ).fetchall()
    con.close()
    db = {}
    for r in rows:
        try:
            answers = json.loads(r["answers"])
        except Exception:
            answers = [r["answers"]]
        db[r["id"]] = {
            "book_id": r["book_id"],
            "question": norm(r["question"]),
            "answers": [norm(a) for a in answers],
            "true_answer": norm(r["true_answer"]),
            "image": (r["image"] or "").strip(),
        }
    return db


def q_key(question, answers):
    """Content key ignoring correct-answer: canonical question + sorted answer set."""
    return (canon(question), tuple(sorted(canon(a) for a in answers)))


def full_key(question, answers, correct_text):
    return q_key(question, answers) + (canon(correct_text),)


def main():
    only_books = [int(x) for x in sys.argv[1:]] if len(sys.argv) > 1 else list(range(1, 11))
    db = load_db()
    db_by_book = {}
    for qid, q in db.items():
        db_by_book.setdefault(q["book_id"], {})[qid] = q

    report = {}
    totals = {"wrong_answer": 0, "missing_in_db": 0, "extra_in_db": 0, "perfect": 0}

    for book_id in only_books:
        cells = parse_book(BOOKS_DIR / BOOK_PDF[book_id])
        db_rows = db_by_book.get(book_id, {})

        # Build full-key multisets (question + answers + correct answer text).
        # A discrepancy in ANY of the three surfaces as an unmatched key.
        book_full = {}
        for c in cells:
            if c["correct"] and 1 <= c["correct"] <= len(c["answers"]):
                ct = c["answers"][c["correct"] - 1]
            else:
                ct = ""
            fk = full_key(c["question"], c["answers"], ct)
            book_full.setdefault(fk, []).append(c)

        db_full = {}
        for qid, d in db_rows.items():
            fk = full_key(d["question"], d["answers"], d["true_answer"])
            db_full.setdefault(fk, []).append(qid)

        # remove matched (multiset intersection)
        book_left, db_left = [], []
        for fk, items in book_full.items():
            extra_n = len(items) - len(db_full.get(fk, []))
            if extra_n > 0:
                book_left.extend(items[-extra_n:])
        for fk, qids in db_full.items():
            extra_n = len(qids) - len(book_full.get(fk, []))
            if extra_n > 0:
                db_left.extend(qids[-extra_n:])

        # classify leftovers by question+answers (ignoring correct) to split
        # "wrong correct answer" from genuine question/answer content differences.
        book_q = {}
        for c in book_left:
            book_q.setdefault(q_key(c["question"], c["answers"]), []).append(c)
        db_q = {}
        for qid in db_left:
            d = db_rows[qid]
            db_q.setdefault(q_key(d["question"], d["answers"]), []).append(qid)

        wrong_answer, missing_in_db, extra_in_db = [], [], []
        for qk, items in book_q.items():
            db_items = db_q.get(qk, [])
            paired = min(len(items), len(db_items))
            for i in range(paired):
                c = items[i]
                qid = db_items[i]
                d = db_rows[qid]
                bk_ct = (c["answers"][c["correct"] - 1]
                         if c["correct"] and 1 <= c["correct"] <= len(c["answers"]) else None)
                wrong_answer.append({
                    "db_id": qid, "book_number": c["number"],
                    "question": d["question"],
                    "db_correct": d["true_answer"], "book_correct": bk_ct,
                })
            for c in items[paired:]:  # book question not present in DB
                missing_in_db.append({
                    "book_number": c["number"], "question": c["question"],
                    "answers": c["answers"],
                    "correct": (c["answers"][c["correct"] - 1]
                                if c["correct"] and 1 <= c["correct"] <= len(c["answers"]) else None),
                })
        for qk, qids in db_q.items():
            paired = min(len(book_q.get(qk, [])), len(qids))
            for qid in qids[paired:]:  # DB question not present in book
                d = db_rows[qid]
                extra_in_db.append({
                    "db_id": qid, "question": d["question"],
                    "answers": d["answers"], "correct": d["true_answer"],
                })

        report[book_id] = {
            "db_count": len(db_rows), "parsed_cells": len(cells),
            "wrong_answer": wrong_answer,
            "missing_in_db": missing_in_db,
            "extra_in_db": extra_in_db,
        }
        totals["wrong_answer"] += len(wrong_answer)
        totals["missing_in_db"] += len(missing_in_db)
        totals["extra_in_db"] += len(extra_in_db)

        status = "OK" if not (wrong_answer or missing_in_db or extra_in_db) else "DIFF"
        print(f"BOOK {book_id:2d}: db={len(db_rows):4d} parsed={len(cells):4d} | "
              f"wrong_answer={len(wrong_answer):3d} missing_in_db={len(missing_in_db):3d} "
              f"extra_in_db={len(extra_in_db):3d}  [{status}]")

    print("\n===== TOTALS =====")
    for k, v in totals.items():
        print(f"{k}: {v}")

    out = REPO / "scripts/verify_report.json"
    out.write_text(json.dumps(report, ensure_ascii=False, indent=2))
    print(f"\nDetails -> {out}")


if __name__ == "__main__":
    main()
