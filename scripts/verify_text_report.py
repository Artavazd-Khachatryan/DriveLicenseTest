#!/usr/bin/env python3
"""Render scripts/verify_report.json into a human-readable Markdown diff report.

Pairs each "missing_in_db" book cell with the most similar "extra_in_db" DB row
within the same book and shows the character-level difference, so genuine
discrepancies are easy to eyeball.
"""
import difflib
import json
import unicodedata
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
REPORT = REPO / "scripts/verify_report.json"
OUT = REPO / "scripts/verify_text_report.md"


def canon(s):
    s = unicodedata.normalize("NFC", s or "").casefold()
    return "".join(c for c in s if c.isalnum() and not (0x0559 <= ord(c) <= 0x055F))


def sig(rec, ans_key="answers"):
    return canon(rec["question"]) + "".join(sorted(canon(a) for a in rec[ans_key]))


def main():
    r = json.loads(REPORT.read_text())
    lines = ["# Text verification: questions/answers vs source books", ""]
    total_pairs = total_unpaired = 0

    for bid in sorted(r, key=int):
        b = r[bid]
        miss = list(b["missing_in_db"])   # book cells not exactly in DB
        extra = list(b["extra_in_db"])    # DB rows not exactly in book
        if not miss and not extra:
            continue
        lines.append(f"## Book {bid}  (db={b['db_count']}, parsed={b['parsed_cells']})")
        used = set()
        rows = []
        for m in miss:
            best, bs = -1, -1.0
            for i, e in enumerate(extra):
                if i in used:
                    continue
                rr = difflib.SequenceMatcher(None, sig(m), sig(e)).ratio()
                if rr > bs:
                    bs, best = rr, i
            if best < 0 or bs < 0.55:
                rows.append((bs, m, None))
                continue
            used.add(best)
            rows.append((bs, m, extra[best]))
        # DB rows that never paired
        for i, e in enumerate(extra):
            if i not in used:
                rows.append((-1.0, None, e))

        rows.sort(key=lambda x: x[0])
        for bs, m, e in rows:
            if m and e:
                total_pairs += 1
                lines.append(f"\n- **similarity {bs:.2f}** · book#{m.get('number') or m.get('book_number')} ↔ db id {e['db_id']}")
                if canon(m["question"]) != canon(e["question"]):
                    lines.append(f"  - Q book: {m['question']}")
                    lines.append(f"  - Q db  : {e['question']}")
                if sorted(canon(a) for a in m["answers"]) != sorted(canon(a) for a in e["answers"]):
                    lines.append(f"  - A book: {m['answers']}")
                    lines.append(f"  - A db  : {e['answers']}")
            elif m:
                total_unpaired += 1
                lines.append(f"\n- **book-only (no DB match)** book#{m.get('number') or m.get('book_number')}: {m['question']}")
                lines.append(f"  - answers: {m['answers']}")
            elif e:
                total_unpaired += 1
                lines.append(f"\n- **DB-only (no book match)** id {e['db_id']}: {e['question']}")
                lines.append(f"  - answers: {e['answers']}")
        lines.append("")

    header = [f"_paired near-misses: {total_pairs}, unpaired: {total_unpaired}_", ""]
    OUT.write_text("\n".join(lines[:2] + header + lines[2:]), encoding="utf-8")
    print(f"Wrote {OUT}  (paired={total_pairs}, unpaired={total_unpaired})")


if __name__ == "__main__":
    main()
