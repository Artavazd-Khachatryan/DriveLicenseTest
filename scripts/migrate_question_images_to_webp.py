#!/usr/bin/env python3
"""Convert bundled question PNGs to WebP and refresh DB image references.

Steps:
1. Convert question*_image.png -> question*_image.webp (cwebp).
2. Copy originals to scripts/review/webp-migration/originals/ for QA HTML.
3. Update Question.image in the bundled DB (.png -> .webp).
4. Remove question*_image.png from drawables.
5. Write scripts/review/webp-migration/quality_review.html (PNG vs WebP).

Usage:
    python3 scripts/migrate_question_images_to_webp.py [--quality 85] [--dry-run]
"""

from __future__ import annotations

import argparse
import html
import json
import shutil
import sqlite3
import subprocess
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
DB_PATH = REPO_ROOT / "database/src/commonMain/resources/license_test_questions.db"
DRAWABLE_DIR = REPO_ROOT / "ui/src/commonMain/composeResources/drawable"
REVIEW_DIR = REPO_ROOT / "scripts/review/webp-migration"
ORIGINALS_DIR = REVIEW_DIR / "originals"
OUT_HTML = REVIEW_DIR / "quality_review.html"


def run_cwebp(src: Path, dst: Path, quality: int) -> None:
    dst.parent.mkdir(parents=True, exist_ok=True)
    proc = subprocess.run(
        ["cwebp", "-q", str(quality), "-m", "6", str(src), "-o", str(dst)],
        capture_output=True,
        text=True,
    )
    if proc.returncode != 0:
        raise RuntimeError(f"cwebp failed for {src.name}: {proc.stderr.strip()}")


def update_db_image_refs() -> int:
    conn = sqlite3.connect(DB_PATH)
    rows = conn.execute(
        "SELECT id, image FROM Question WHERE image LIKE '%.png'"
    ).fetchall()
    for qid, image in rows:
        if not image.endswith(".png"):
            continue
        conn.execute(
            "UPDATE Question SET image = ? WHERE id = ?",
            (image[:-4] + ".webp", qid),
        )
    conn.commit()
    conn.close()
    return len(rows)


def write_quality_review(cards: list[dict], quality: int) -> None:
    REVIEW_DIR.mkdir(parents=True, exist_ok=True)

    def esc(s: str) -> str:
        return html.escape(s, quote=True)

    html_cards = []
    total_png = 0
    total_webp = 0
    for c in cards:
        total_png += c["png_bytes"]
        total_webp += c["webp_bytes"]
        savings = 100 * (1 - c["webp_bytes"] / c["png_bytes"]) if c["png_bytes"] else 0
        html_cards.append(
            f"""
            <section class="card" id="q{c['id']}" data-id="{c['id']}">
              <header class="card-head">
                <div class="qid">#{c['id']}</div>
                <div class="sizes">
                  PNG {c['png_kb']} KB → WebP {c['webp_kb']} KB
                  <span class="save">−{savings:.0f}%</span>
                </div>
              </header>
              <p class="qtext">{esc(c['question'])}</p>
              <div class="compare">
                <figure>
                  <figcaption>Original PNG</figcaption>
                  <img loading="lazy" src="{esc(c['png_rel'])}" alt="question {c['id']} png" />
                </figure>
                <figure>
                  <figcaption>WebP q{quality} (app size)</figcaption>
                  <img loading="lazy" class="app-size" src="{esc(c['webp_rel'])}" alt="question {c['id']} webp" />
                </figure>
              </div>
            </section>
            """
        )

    saved_pct = 100 * (1 - total_webp / total_png) if total_png else 0
    OUT_HTML.write_text(
        f"""<!doctype html>
<html lang="hy">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>WebP migration quality review</title>
  <style>
    :root {{
      color-scheme: light dark;
      --bg: #0b1020;
      --card: rgba(255,255,255,0.06);
      --border: rgba(255,255,255,0.12);
      --muted: rgba(255,255,255,0.72);
      --accent: #60a5fa;
    }}
    * {{ box-sizing: border-box; }}
    body {{
      margin: 0;
      font-family: -apple-system, system-ui, Segoe UI, Roboto, Helvetica, Arial, sans-serif;
      background: var(--bg);
      color: white;
    }}
    header.page {{
      position: sticky;
      top: 0;
      z-index: 10;
      backdrop-filter: blur(10px);
      background: rgba(11,16,32,0.9);
      border-bottom: 1px solid var(--border);
      padding: 12px 16px;
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
    }}
    input, select {{
      padding: 10px 12px;
      border-radius: 10px;
      border: 1px solid var(--border);
      background: rgba(255,255,255,0.06);
      color: white;
      outline: none;
    }}
    input {{ min-width: 240px; }}
    .hint {{ color: var(--muted); font-size: 13px; }}
    .summary {{ color: var(--accent); font-size: 13px; font-weight: 600; }}
    main {{ padding: 16px; max-width: 1280px; margin: 0 auto; }}
    .card {{
      border: 1px solid var(--border);
      border-radius: 16px;
      background: var(--card);
      padding: 16px;
      margin-bottom: 14px;
    }}
    .card-head {{
      display: flex;
      justify-content: space-between;
      gap: 12px;
      flex-wrap: wrap;
      margin-bottom: 8px;
    }}
    .qid {{ font-weight: 700; font-size: 16px; }}
    .sizes {{ font-size: 13px; color: var(--muted); }}
    .save {{ color: #4ade80; font-weight: 600; margin-left: 6px; }}
    .qtext {{ margin: 0 0 12px; line-height: 1.35; font-size: 15px; }}
    .compare {{
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 14px;
    }}
    @media (max-width: 900px) {{
      .compare {{ grid-template-columns: 1fr; }}
    }}
    figure {{
      margin: 0;
      border: 1px solid var(--border);
      border-radius: 12px;
      overflow: hidden;
      background: rgba(255,255,255,0.03);
    }}
    figcaption {{
      padding: 8px 10px;
      font-size: 12px;
      color: var(--muted);
      border-bottom: 1px solid var(--border);
    }}
    img {{
      display: block;
      width: 100%;
      height: auto;
      background: #111827;
    }}
    img.app-size {{
      width: min(100%, 360px);
      margin: 0 auto;
    }}
    .hidden {{ display: none !important; }}
  </style>
</head>
<body>
  <header class="page">
    <input id="q" placeholder="Search id or question text" />
    <select id="sort">
      <option value="id">Sort: question id</option>
      <option value="savings">Sort: biggest savings</option>
      <option value="webp">Sort: largest WebP</option>
    </select>
    <div class="summary">{len(cards)} images · {total_png/1024/1024:.1f} MB PNG → {total_webp/1024/1024:.1f} MB WebP (−{saved_pct:.0f}%)</div>
    <div class="hint">Right column shows WebP at ~app display width. Open via file:// after migration.</div>
  </header>
  <main id="list">{''.join(html_cards)}</main>
  <script>
    const input = document.getElementById('q');
    const sort = document.getElementById('sort');
    const list = document.getElementById('list');
    let cards = Array.from(document.querySelectorAll('.card'));

    function normalize(s) {{
      return (s || '').toLowerCase().trim();
    }}

    function applyFilter() {{
      const q = normalize(input.value);
      cards.forEach(c => {{
        const hay = normalize(c.innerText);
        c.classList.toggle('hidden', q && !hay.includes(q));
      }});
    }}

    function applySort() {{
      const mode = sort.value;
      const visible = cards.slice();
      visible.sort((a, b) => {{
        if (mode === 'savings') {{
          const sa = parseFloat(a.querySelector('.save').textContent.replace(/[^0-9.-]/g, ''));
          const sb = parseFloat(b.querySelector('.save').textContent.replace(/[^0-9.-]/g, ''));
          return sb - sa;
        }}
        if (mode === 'webp') {{
          const wa = parseInt(a.querySelector('.sizes').textContent.match(/WebP (\\d+)/)[1], 10);
          const wb = parseInt(b.querySelector('.sizes').textContent.match(/WebP (\\d+)/)[1], 10);
          return wb - wa;
        }}
        return parseInt(a.dataset.id, 10) - parseInt(b.dataset.id, 10);
      }});
      visible.forEach(c => list.appendChild(c));
      cards = visible;
      applyFilter();
    }}

    input.addEventListener('input', applyFilter);
    sort.addEventListener('change', applySort);
    input.addEventListener('keydown', (e) => {{
      if (e.key === 'Enter') {{
        const n = parseInt(normalize(input.value), 10);
        if (!Number.isNaN(n)) {{
          const el = document.getElementById('q' + n);
          if (el) el.scrollIntoView({{ behavior: 'smooth', block: 'start' }});
        }}
      }}
    }});
  </script>
</body>
</html>
""",
        encoding="utf-8",
    )


def load_questions() -> dict[int, str]:
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    rows = conn.execute("SELECT id, question FROM Question ORDER BY id").fetchall()
    conn.close()
    return {int(r["id"]): r["question"] or "" for r in rows}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--quality", type=int, default=85, help="WebP quality (default: 85)")
    parser.add_argument("--dry-run", action="store_true", help="Convert only; do not update DB or delete PNGs")
    args = parser.parse_args()

    if shutil.which("cwebp") is None:
        print("error: cwebp not found (install via: brew install webp)")
        return 1

    png_files = sorted(DRAWABLE_DIR.glob("question*_image.png"))
    if not png_files:
        print("No question*_image.png files found — nothing to migrate.")
        return 0

    questions = load_questions()
    cards: list[dict] = []
    converted = 0

    for png_path in png_files:
        qid = int(png_path.stem.replace("question", "").replace("_image", ""))
        webp_name = f"question{qid}_image.webp"
        webp_path = DRAWABLE_DIR / webp_name
        original_copy = ORIGINALS_DIR / png_path.name

        if not args.dry_run:
            ORIGINALS_DIR.mkdir(parents=True, exist_ok=True)
            if not original_copy.exists():
                shutil.copy2(png_path, original_copy)

        run_cwebp(png_path, webp_path, args.quality)
        converted += 1

        png_bytes = png_path.stat().st_size
        webp_bytes = webp_path.stat().st_size
        cards.append(
            {
                "id": qid,
                "question": questions.get(qid, ""),
                "png_bytes": png_bytes,
                "webp_bytes": webp_bytes,
                "png_kb": png_bytes // 1024,
                "webp_kb": webp_bytes // 1024,
                "png_rel": f"originals/{png_path.name}",
                "webp_rel": f"../../ui/src/commonMain/composeResources/drawable/{webp_name}",
            }
        )

        if not args.dry_run:
            png_path.unlink()

    write_quality_review(cards, args.quality)

    updated = 0
    if not args.dry_run:
        updated = update_db_image_refs()

    png_total = sum(c["png_bytes"] for c in cards)
    webp_total = sum(c["webp_bytes"] for c in cards)
    print(f"Converted {converted} PNGs to WebP q{args.quality}")
    print(f"Size: {png_total/1024/1024:.1f} MB -> {webp_total/1024/1024:.1f} MB ({100*webp_total/png_total:.0f}%)")
    print(f"Quality review: {OUT_HTML}")
    if not args.dry_run:
        print(f"Updated {updated} DB image references")
        print(f"Originals kept at: {ORIGINALS_DIR}")
    else:
        print("Dry run: PNGs and DB unchanged")
    return 0


if __name__ == "__main__":
    sys.exit(main())
