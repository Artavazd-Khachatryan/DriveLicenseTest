#!/usr/bin/env python3
"""Build a side-by-side gallery: each question's figure from the book PDF next to
its DB drawable, for human verification that images match the books.

Mapping: every question figure sits in a grid cell whose footer prints the question
number (== DB id). In "words" mode each footer number has its own x/y, so we can map
figure -> nearest footer-number-above-in-same-column -> DB id -> question{id}_image.png.
Footer numbers are told apart from answer-content numbers by filtering to each book's
known DB id range.
"""
import html
import json
import sqlite3
from pathlib import Path

import fitz

REPO = Path(__file__).resolve().parents[1]
DB_PATH = REPO / "database/src/commonMain/resources/license_test_questions.db"
DRAWABLE = REPO / "ui/src/commonMain/composeResources/drawable"
BOOKS_DIR = Path("/Users/artavazd/Documents/driver_license_test/Driver License Test App")
BOOK_PDF = {i: f"խումբ {i}.pdf" for i in range(1, 11)}
OUT = REPO / "scripts/image_gallery.html"
FIG_DIR = REPO / "scripts/book_figures"          # extracted book figures
DRAWABLE_REL = "../ui/src/commonMain/composeResources/drawable"
MID_X = 300.0


def db_info():
    con = sqlite3.connect(str(DB_PATH))
    rows = con.execute("SELECT id, book_id, question, image FROM Question ORDER BY id").fetchall()
    con.close()
    by_id, ranges = {}, {}
    for qid, book_id, q, img in rows:
        by_id[qid] = {"book_id": book_id, "question": q, "image": (img or "").strip()}
        lo, hi = ranges.get(book_id, (qid, qid))
        ranges[book_id] = (min(lo, qid), max(hi, qid))
    return by_id, ranges


def extract_figures(book_id, lo, hi, need_image=None):
    """Return {db_id: (PNG bytes, kind)} for figures from the book PDF.

    kind is "embedded" (raster image extracted directly) or "region" (the cell area
    rasterized as a fallback when no embedded raster paired — e.g. vector drawings or
    photos the pairing missed). `need_image` is the set of db ids the DB marks as
    having an image; region fallback is only rendered for those.
    """
    need_image = need_image or set()
    doc = fitz.open(str(BOOKS_DIR / BOOK_PDF[book_id]))
    figures = {}
    for pg in doc:
        # footer numbers within this book's id range, with positions
        nums = []
        for x0, y0, x1, y1, txt, *_ in pg.get_text("words"):
            t = txt.strip()
            if t.isdigit() and lo <= int(t) <= hi:
                nums.append((int(t), (x0 + x1) / 2, y0))
        # pass 1: embedded raster images -> nearest footer number below in same column
        for im in pg.get_image_info(xrefs=True):
            bx0, by0, bx1, by1 = im["bbox"]
            col = (bx0 + bx1) / 2 < MID_X
            cands = [(n, ny) for (n, nx, ny) in nums
                     if (nx < MID_X) == col and ny >= by1 - 5]
            xref = im.get("xref")
            if not cands or not xref:
                continue
            qid = min(cands, key=lambda c: c[1])[0]
            if qid in figures:
                continue
            try:
                pix = fitz.Pixmap(doc, xref)
                if pix.n - pix.alpha >= 4:
                    pix = fitz.Pixmap(fitz.csRGB, pix)
                figures[qid] = (pix.tobytes("png"), "embedded")
            except Exception:
                pass
        # pass 2: region fallback for DB-image cells with no embedded figure paired
        col_nums = {True: sorted([(ny, n) for n, nx, ny in nums if nx < MID_X]),
                    False: sorted([(ny, n) for n, nx, ny in nums if nx >= MID_X])}
        for col, lst in col_nums.items():
            x0, x1 = (40, 298) if col else (300, 558)
            prev_y = 45.0
            for ny, n in lst:
                if n in need_image and n not in figures:
                    clip = fitz.Rect(x0, prev_y, x1, ny)
                    pix = pg.get_pixmap(clip=clip, dpi=130)
                    figures[n] = (pix.tobytes("png"), "region")
                prev_y = ny
    return figures


def save_fig(qid, data):
    """Write a book figure to FIG_DIR and return its relative src for the HTML."""
    (FIG_DIR / f"{qid}.png").write_bytes(data)
    return f"book_figures/{qid}.png"


def main():
    by_id, ranges = db_info()
    FIG_DIR.mkdir(exist_ok=True)
    cards = []
    missing_img_cards = []
    stats = {"db_with_image": 0, "figure_found": 0, "drawable_found": 0,
             "no_figure": [], "no_drawable": [], "db_missing_image": []}

    for book_id in range(1, 11):
        lo, hi = ranges[book_id]
        need = {qid for qid in range(lo, hi + 1)
                if by_id.get(qid) and by_id[qid]["image"]}
        figures = extract_figures(book_id, lo, hi, need)

        # questions the book illustrates but the DB stores with no image
        for qid in sorted(figures):
            if figures[qid][1] == "embedded" and qid not in need:
                stats["db_missing_image"].append(qid)
                missing_img_cards.append(f"""
                <div class="card warn">
                  <div class="hdr">#{qid} <span class="book">book {book_id}</span>
                   <span class="tag">DB has NO image</span></div>
                  <div class="q">{html.escape(by_id[qid]['question'])}</div>
                  <div class="pair"><figure><figcaption>BOOK figure (not in DB)</figcaption>
                  <img src="{save_fig(qid, figures[qid][0])}"></figure></div>
                </div>""")

        for qid in range(lo, hi + 1):
            info = by_id.get(qid)
            if not info or not info["image"]:
                continue  # text-only question
            stats["db_with_image"] += 1

            fig = figures.get(qid)
            if fig:
                stats["figure_found"] += 1
                tag = "" if fig[1] == "embedded" else ' <span class="tag">region render</span>'
                book_img = f'<img src="{save_fig(qid, fig[0])}" loading="lazy">'
            else:
                stats["no_figure"].append(qid)
                tag = ""
                book_img = '<div class="missing">no figure found in book</div>'

            draw_path = DRAWABLE / info["image"]
            if draw_path.exists():
                stats["drawable_found"] += 1
                db_img = f'<img src="{DRAWABLE_REL}/{html.escape(info["image"])}" loading="lazy">'
            else:
                stats["no_drawable"].append(qid)
                db_img = f'<div class="missing">drawable missing: {html.escape(info["image"])}</div>'

            cards.append(f"""
            <div class="card">
              <div class="hdr">#{qid} <span class="book">book {book_id}</span>{tag}</div>
              <div class="q">{html.escape(info['question'])}</div>
              <div class="pair">
                <figure><figcaption>BOOK</figcaption>{book_img}</figure>
                <figure><figcaption>DB ({html.escape(info['image'])})</figcaption>{db_img}</figure>
              </div>
            </div>""")

    doc = f"""<!doctype html><meta charset="utf-8">
<title>Image verification gallery</title>
<style>
 body{{font-family:system-ui,Arial;margin:0;background:#f5f5f5}}
 header{{position:sticky;top:0;background:#1f2937;color:#fff;padding:12px 20px;z-index:9}}
 header b{{color:#93c5fd}}
 .grid{{display:flex;flex-direction:column;gap:14px;padding:20px;max-width:1100px;margin:auto}}
 .card{{background:#fff;border-radius:10px;padding:14px;box-shadow:0 1px 4px rgba(0,0,0,.1)}}
 .hdr{{font-weight:700;font-size:15px}} .book{{color:#888;font-weight:400;font-size:12px}}
 .q{{margin:6px 0 10px;font-size:14px;color:#333}}
 .pair{{display:grid;grid-template-columns:1fr 1fr;gap:12px}}
 figure{{margin:0;border:1px solid #e5e7eb;border-radius:8px;padding:8px;text-align:center}}
 figcaption{{font-size:11px;color:#6b7280;letter-spacing:.05em;margin-bottom:6px}}
 img{{max-width:100%;height:auto;border-radius:4px}}
 .missing{{color:#b91c1c;font-size:13px;padding:30px 8px;background:#fef2f2;border-radius:4px}}
 .tag{{font-size:11px;background:#fde68a;color:#92400e;padding:2px 6px;border-radius:4px;margin-left:6px}}
 .card.warn{{border:2px solid #f59e0b}}
 h2{{max-width:1100px;margin:24px auto 0;padding:0 20px;color:#92400e}}
</style>
<header>Image gallery — {stats['db_with_image']} image questions ·
 figures matched <b>{stats['figure_found']}</b> ·
 drawables present <b>{stats['drawable_found']}</b> ·
 no figure <b>{len(stats['no_figure'])}</b> ·
 no drawable <b>{len(stats['no_drawable'])}</b> ·
 DB missing image <b>{len(stats['db_missing_image'])}</b></header>
{f'<h2>⚠ {len(missing_img_cards)} questions illustrated in the book but with NO image in the DB</h2><div class="grid">{"".join(missing_img_cards)}</div>' if missing_img_cards else ''}
<h2>All image questions (book vs DB)</h2>
<div class="grid">{''.join(cards)}</div>"""

    OUT.write_text(doc, encoding="utf-8")
    print(json.dumps({k: (v if not isinstance(v, list) else f"{len(v)} -> {v[:20]}")
                      for k, v in stats.items()}, ensure_ascii=False, indent=2))
    print(f"\nGallery -> {OUT}")


if __name__ == "__main__":
    main()
