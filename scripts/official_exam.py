#!/usr/bin/env python3
"""Official RA driving-exam question bank (roadpolice.am ABC + DT PDFs).

Single-column layout (from 2026-09-01 / PDFs last-modified 2026-10-02):
question N., answers 1. 2. …, then Պատ.՝ K. Shared between the sync and
verify scripts so fetch/parse/canon stay identical.

Cache (not in git): $OFFICIAL_EXAM_CACHE or /tmp/official-exam-2026
"""
from __future__ import annotations

import os
import re
import unicodedata
import urllib.request
from dataclasses import dataclass, field
from pathlib import Path

import fitz

REPO = Path(__file__).resolve().parents[1]
DEFAULT_CACHE = Path(os.environ.get("OFFICIAL_EXAM_CACHE", "/tmp/official-exam-2026"))
PAPERS = ("abc", "dt")
BOOKS = range(1, 11)
SOURCE_BASE = "https://roadpolice.am/exam/{paper}/hy/{book}.pdf"

PAT_RE = re.compile(r"Պատ\s*[.․]?\s*՝\s*(\d+)")
QNUM_RE = re.compile(r"^\s*(\d+)\.\s*(.*)$", re.S)
LABEL_RE = re.compile(r"^\s*(\d+)\.\s*(.*)$")
COUNT_RE = re.compile(r"(\d+)\s+հարց")
HEADER_LINE_RE = re.compile(
    r"^\s*(?:ԽՈՒՄԲ\s+\d+"
    r"|ABC\s+կարգեր.*|DT\s+կարգեր.*"
    r"|\d+\s+հարց"
    r"|էջ\s+\d+\s*/\s*\d+"
    r"|տեսական\s+հարցաշար"
    r"|հարցաշար"
    r")\s*$"
)
LATIN_TO_ARM = str.maketrans("ABCDEabcde", "ԱԲԳԴԵաբգդե")


@dataclass
class OfficialRecord:
    paper: str  # ABC | DT
    book: int
    number: int
    question: str
    answers: list[str]
    correct: int  # 1-based
    pdf_path: Path
    page_index: int | None = None
    q_bottom: float | None = None
    ans_top: float | None = None
    image_bboxes: list[tuple[float, float, float, float]] = field(default_factory=list)

    @property
    def correct_text(self) -> str:
        if 1 <= self.correct <= len(self.answers):
            return self.answers[self.correct - 1]
        return ""


def norm(s: str) -> str:
    s = unicodedata.normalize("NFC", s or "")
    s = s.replace("\u00a0", " ").replace("\u202f", " ")
    # PDFs emit ASCII << >> where the book uses Armenian guillemets.
    s = s.replace("<< ", "«").replace("<<", "«")
    s = s.replace(" >>", "»").replace(">>", "»")
    s = re.sub(r"\s+", " ", s).strip()
    return s


def canon(s: str) -> str:
    """Match key: Armenian letters + digits, և collapsed, Latin A–E as Ա–Ե."""
    s = unicodedata.normalize("NFC", s or "").casefold()
    s = s.replace("և", "եւ")
    s = s.translate(LATIN_TO_ARM)
    return "".join(
        ch for ch in s if ch.isalnum() and not (0x0559 <= ord(ch) <= 0x055F)
    )


def content_key(question: str, answers: list[str]) -> tuple:
    return (canon(question), tuple(sorted(canon(a) for a in answers)))


def question_key(question: str) -> str:
    return canon(question)


def pdf_url(paper: str, book: int) -> str:
    return SOURCE_BASE.format(paper=paper.lower(), book=book)


def pdf_path(cache: Path, paper: str, book: int) -> Path:
    return cache / paper.lower() / f"{book}.pdf"


def fetch_pdfs(cache: Path = DEFAULT_CACHE, refresh: bool = False) -> None:
    cache.mkdir(parents=True, exist_ok=True)
    opener = urllib.request.build_opener()
    opener.addheaders = [("User-Agent", "DriveLicenseTest-official-sync/1.0")]
    for paper in PAPERS:
        (cache / paper).mkdir(parents=True, exist_ok=True)
        for book in BOOKS:
            dest = pdf_path(cache, paper, book)
            if dest.exists() and dest.stat().st_size > 1000 and not refresh:
                continue
            url = pdf_url(paper, book)
            print(f"fetch {url}")
            with opener.open(url, timeout=60) as resp:
                data = resp.read()
            dest.write_bytes(data)
            print(f"  -> {dest} ({len(data)} bytes)")


def _is_header_line(line: str) -> bool:
    return bool(HEADER_LINE_RE.match(line.strip()))


def _parse_cell(raw: str) -> tuple[int, str, list[str], int]:
    m = PAT_RE.search(raw)
    if not m:
        raise ValueError(f"no Պատ.՝ marker in cell: {raw[:80]!r}")
    correct = int(m.group(1))
    body = raw[: m.start()].strip()
    lines = body.split("\n")
    if not lines:
        raise ValueError("empty cell")
    first = lines[0]
    qm = QNUM_RE.match(first)
    if not qm:
        # number and question on following lines
        joined = "\n".join(lines)
        qm = QNUM_RE.match(joined)
        if not qm:
            raise ValueError(f"no question number: {first!r}")
        rest = qm.group(2)
        number = int(qm.group(1))
        q_lines, ans_src = [], rest.split("\n")
    else:
        number = int(qm.group(1))
        q_lines = [qm.group(2)] if qm.group(2) else []
        ans_src = lines[1:]

    answers: list[str] = []
    cur = None
    expect = 1
    for line in ans_src:
        mm = LABEL_RE.match(line)
        if mm and int(mm.group(1)) == expect:
            if cur is not None:
                answers.append(norm(cur))
            cur = mm.group(2)
            expect += 1
        elif cur is not None:
            cur = (cur + " " + line).strip()
        else:
            q_lines.append(line)
    if cur is not None:
        answers.append(norm(cur))
    question = norm(" ".join(q_lines))
    if not question:
        raise ValueError(f"empty question text for #{number}")
    if not answers:
        raise ValueError(f"no answers for #{number}")
    if not (1 <= correct <= len(answers)):
        raise ValueError(
            f"#{number}: Պատ.՝ {correct} out of range for {len(answers)} answers"
        )
    return number, question, answers, correct


def parse_book(pdf_path: Path, paper: str) -> list[OfficialRecord]:
    paper = paper.upper()
    book = int(pdf_path.stem)
    doc = fitz.open(str(pdf_path))
    header_n = None
    page_texts: list[str] = []
    page_blocks: list[list[tuple]] = []
    page_images: list[list[tuple[float, float, float, float]]] = []
    for pg in doc:
        text = pg.get_text()
        if header_n is None:
            cm = COUNT_RE.search(text)
            if cm:
                header_n = int(cm.group(1))
        kept = [ln for ln in text.splitlines() if not _is_header_line(ln)]
        page_texts.append("\n".join(kept))
        page_blocks.append(pg.get_text("blocks"))
        page_images.append([tuple(im["bbox"]) for im in pg.get_image_info()])

    body = "\n".join(page_texts)
    chunks = []
    last = 0
    for m in PAT_RE.finditer(body):
        chunks.append(body[last : m.end()])
        last = m.end()
    leftover = body[last:].strip()
    if leftover:
        # trailing junk after last Պատ should be empty once headers are stripped
        if PAT_RE.search(leftover):
            raise ValueError(f"{pdf_path}: leftover still contains Պատ: {leftover[:80]!r}")

    records: list[OfficialRecord] = []
    for chunk in chunks:
        number, question, answers, correct = _parse_cell(chunk)
        rec = OfficialRecord(
            paper=paper,
            book=book,
            number=number,
            question=question,
            answers=answers,
            correct=correct,
            pdf_path=pdf_path,
        )
        _attach_geometry(rec, page_blocks, page_images)
        records.append(rec)

    numbers = [r.number for r in records]
    if numbers != list(range(1, len(records) + 1)):
        raise ValueError(
            f"{pdf_path}: expected sequential 1..{len(records)}, got {numbers[:5]}…{numbers[-3:]}"
        )
    if header_n is not None and header_n != len(records):
        raise ValueError(
            f"{pdf_path}: header says {header_n} questions, parsed {len(records)}"
        )
    return records


def _attach_geometry(
    rec: OfficialRecord,
    page_blocks: list[list[tuple]],
    page_images: list[list[tuple[float, float, float, float]]],
) -> None:
    """Locate the question block and the first answer on the same (or next) page."""
    q_pat = re.compile(rf"^{rec.number}\.\s")
    for pi, blocks in enumerate(page_blocks):
        q_block = None
        for b in blocks:
            x0, y0, x1, y1, txt = b[0], b[1], b[2], b[3], (b[4] or "")
            if x0 > 58:
                continue
            if q_pat.match(txt.strip()):
                q_block = (x0, y0, x1, y1)
                break
        if q_block is None:
            continue
        rec.page_index = pi
        rec.q_bottom = q_block[3]
        # first answer "1." after the question, typically indented
        ans_top = None
        for b in blocks:
            x0, y0, x1, y1, txt = b[0], b[1], b[2], b[3], (b[4] or "")
            if y0 < rec.q_bottom - 1:
                continue
            t = txt.strip()
            if t.startswith("1.") or t.startswith("1.\n"):
                ans_top = y0
                break
        if ans_top is None and pi + 1 < len(page_blocks):
            rec.page_index = pi  # image may sit under the question on this page
            for b in page_blocks[pi + 1]:
                x0, y0, x1, y1, txt = b[0], b[1], b[2], b[3], (b[4] or "")
                t = txt.strip()
                if t.startswith("1.") or t.startswith("1.\n"):
                    # answers on next page: take images below question on this page
                    ans_top = page_blocks[pi] and 10000.0
                    break
        rec.ans_top = ans_top
        if rec.q_bottom is not None and rec.ans_top is not None and rec.ans_top > rec.q_bottom:
            gap = (rec.q_bottom, rec.ans_top)
            rec.image_bboxes = [
                bb
                for bb in page_images[pi]
                if bb[3] > gap[0] - 4 and bb[1] < gap[1] + 4
            ]
        return


def parse_all(cache: Path = DEFAULT_CACHE) -> list[OfficialRecord]:
    out: list[OfficialRecord] = []
    for paper in PAPERS:
        for book in BOOKS:
            path = pdf_path(cache, paper, book)
            if not path.exists():
                raise FileNotFoundError(f"missing {path}; run fetch first")
            recs = parse_book(path, paper)
            out.extend(recs)
            print(f"parsed {paper.upper()} book {book}: {len(recs)}")
    return out


def _stamp(pick: OfficialRecord, papers: set[str], group: list[OfficialRecord]) -> OfficialRecord:
    pick._papers = papers  # type: ignore[attr-defined]
    pick._group = group  # type: ignore[attr-defined]
    return pick


def unique_official(records: list[OfficialRecord]) -> list[OfficialRecord]:
    """One DB row per distinct official question.

    ABC and DT cells of the same question (same wording, answers, and correct
    index, or the same booklet slot) become GENERAL. Diagram twins that share
    wording but differ in booklet slot / correct answer stay separate rows.
    """
    abc = [r for r in records if r.paper == "ABC"]
    dt = [r for r in records if r.paper == "DT"]
    used_abc: set[int] = set()
    used_dt: set[int] = set()
    unique: list[OfficialRecord] = []

    dt_by_bn = {(d.book, d.number): d for d in dt}
    for a in abc:
        d = dt_by_bn.get((a.book, a.number))
        if d is None:
            continue
        if content_key(a.question, a.answers) != content_key(d.question, d.answers):
            continue
        unique.append(_stamp(a, {"ABC", "DT"}, [a, d]))
        used_abc.add(id(a))
        used_dt.add(id(d))

    def full_key(r: OfficialRecord):
        return content_key(r.question, r.answers) + (canon(r.correct_text),)

    from collections import defaultdict

    abc_left = [a for a in abc if id(a) not in used_abc]
    dt_left = [d for d in dt if id(d) not in used_dt]
    abc_idx: dict = defaultdict(list)
    dt_idx: dict = defaultdict(list)
    for a in abc_left:
        abc_idx[full_key(a)].append(a)
    for d in dt_left:
        dt_idx[full_key(d)].append(d)

    for k, avs in list(abc_idx.items()):
        dvs = list(dt_idx.get(k, []))
        avs = list(avs)
        # pair same book first
        for a in list(avs):
            same = [d for d in dvs if d.book == a.book]
            if len(same) == 1:
                d = same[0]
                unique.append(_stamp(a, {"ABC", "DT"}, [a, d]))
                used_abc.add(id(a))
                used_dt.add(id(d))
                avs.remove(a)
                dvs.remove(d)
        avs.sort(key=lambda x: (x.book, x.number))
        dvs.sort(key=lambda x: (x.book, x.number))
        while avs and dvs:
            a, d = avs.pop(0), dvs.pop(0)
            unique.append(_stamp(a, {"ABC", "DT"}, [a, d]))
            used_abc.add(id(a))
            used_dt.add(id(d))

    for a in abc:
        if id(a) not in used_abc:
            unique.append(_stamp(a, {"ABC"}, [a]))
    for d in dt:
        if id(d) not in used_dt:
            unique.append(_stamp(d, {"DT"}, [d]))
    return unique


def exam_group_for(rec: OfficialRecord) -> str:
    papers = getattr(rec, "_papers", {rec.paper})
    if papers == {"ABC", "DT"}:
        return "GENERAL"
    if papers == {"ABC"}:
        return "ABC"
    if papers == {"DT"}:
        return "DT"
    return "GENERAL"
