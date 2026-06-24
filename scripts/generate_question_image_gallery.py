#!/usr/bin/env python3
import html
import json
import sqlite3
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
DB_PATH = REPO_ROOT / "database/src/commonMain/resources/license_test_questions.db"
DRAWABLE_DIR = REPO_ROOT / "ui/src/commonMain/composeResources/drawable"
OUT_PATH = REPO_ROOT / "scripts/question_image_gallery.html"


def main() -> None:
    if not DB_PATH.exists():
        raise SystemExit(f"DB not found: {DB_PATH}")
    if not DRAWABLE_DIR.exists():
        raise SystemExit(f"Drawable dir not found: {DRAWABLE_DIR}")

    con = sqlite3.connect(str(DB_PATH))
    con.row_factory = sqlite3.Row
    cur = con.cursor()
    cur.execute(
        """
        SELECT id, question, true_answer, answers, image
        FROM Question
        ORDER BY id ASC
        """
    )
    rows = cur.fetchall()
    con.close()

    cards = []
    missing_images = 0

    for r in rows:
        qid = int(r["id"])
        qtext = r["question"] or ""
        image_name = (r["image"] or "").strip()
        true_answer = (r["true_answer"] or "").strip()

        answers = []
        raw_answers = r["answers"]
        if raw_answers:
            try:
                answers = json.loads(raw_answers)
            except Exception:
                answers = [raw_answers]

        img_rel = None
        if image_name:
            img_path = DRAWABLE_DIR / image_name
            if img_path.exists():
                # OUT_PATH is in scripts/, so paths should be relative to that file.
                img_rel = "../ui/src/commonMain/composeResources/drawable/" + image_name
            else:
                missing_images += 1

        cards.append(
            {
                "id": qid,
                "question": qtext,
                "true_answer": true_answer,
                "answers": answers,
                "img_rel": img_rel,
                "image_name": image_name,
            }
        )

    def esc(s: str) -> str:
        return html.escape(s, quote=True)

    html_cards = []
    for c in cards:
        answers_html = "".join(f"<li>{esc(str(a))}</li>" for a in (c["answers"] or []))
        if not answers_html:
            answers_html = "<li><em>(no answers)</em></li>"

        img_html = ""
        if c["img_rel"]:
            img_html = f'<img loading="lazy" src="{esc(c["img_rel"])}" alt="question {c["id"]} image" />'
        elif c["image_name"]:
            img_html = f'<div class="missing">Missing image file: {esc(c["image_name"])}</div>'
        else:
            img_html = '<div class="missing">No image</div>'

        html_cards.append(
            f"""
            <section class="card" id="q{c["id"]}">
              <div class="meta">
                <div class="qid">#{c["id"]}</div>
                <div class="qtext">{esc(c["question"])}</div>
                <div class="answer"><strong>Correct:</strong> {esc(c["true_answer"])}</div>
                <details>
                  <summary>Answers</summary>
                  <ul>{answers_html}</ul>
                </details>
              </div>
              <div class="img">{img_html}</div>
            </section>
            """
        )

    OUT_PATH.write_text(
        f"""<!doctype html>
<html lang="hy">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Question image gallery</title>
  <style>
    :root {{
      color-scheme: light dark;
      --bg: #0b1020;
      --card: rgba(255,255,255,0.06);
      --border: rgba(255,255,255,0.12);
      --muted: rgba(255,255,255,0.72);
    }}
    body {{
      margin: 0;
      font-family: -apple-system, system-ui, Segoe UI, Roboto, Helvetica, Arial, sans-serif;
      background: var(--bg);
      color: white;
    }}
    header {{
      position: sticky;
      top: 0;
      z-index: 10;
      backdrop-filter: blur(10px);
      background: rgba(11,16,32,0.85);
      border-bottom: 1px solid var(--border);
      padding: 12px 16px;
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
    }}
    input {{
      padding: 10px 12px;
      border-radius: 10px;
      border: 1px solid var(--border);
      background: rgba(255,255,255,0.06);
      color: white;
      min-width: 260px;
      outline: none;
    }}
    .hint {{
      color: var(--muted);
      font-size: 13px;
    }}
    main {{
      padding: 16px;
      max-width: 1200px;
      margin: 0 auto;
    }}
    .card {{
      display: grid;
      grid-template-columns: 1fr 420px;
      gap: 16px;
      padding: 16px;
      border: 1px solid var(--border);
      border-radius: 16px;
      background: var(--card);
      margin-bottom: 12px;
    }}
    @media (max-width: 900px) {{
      .card {{
        grid-template-columns: 1fr;
      }}
    }}
    .qid {{
      font-weight: 700;
      font-size: 16px;
      margin-bottom: 6px;
    }}
    .qtext {{
      font-size: 15px;
      line-height: 1.35;
      margin-bottom: 8px;
    }}
    .answer {{
      margin-bottom: 6px;
      color: var(--muted);
      font-size: 13px;
    }}
    details {{
      color: var(--muted);
      font-size: 13px;
    }}
    img {{
      width: 100%;
      height: auto;
      border-radius: 12px;
      border: 1px solid var(--border);
      background: rgba(255,255,255,0.03);
    }}
    .missing {{
      padding: 12px;
      border-radius: 12px;
      border: 1px dashed var(--border);
      color: var(--muted);
      font-size: 13px;
    }}
  </style>
</head>
<body>
  <header>
    <input id="q" placeholder="Search by id or text (e.g. 70 or Հետադարձ)" />
    <div class="hint">Tip: type an id and press Enter to jump. Total: {len(cards)}. Missing images: {missing_images}.</div>
  </header>
  <main id="list">
    {''.join(html_cards)}
  </main>
  <script>
    const input = document.getElementById('q');
    const list = document.getElementById('list');
    const cards = Array.from(document.querySelectorAll('.card'));

    function normalize(s) {{
      return (s || '').toLowerCase().trim();
    }}

    function applyFilter() {{
      const q = normalize(input.value);
      if (!q) {{
        cards.forEach(c => c.style.display = '');
        return;
      }}
      cards.forEach(c => {{
        const hay = normalize(c.innerText);
        c.style.display = hay.includes(q) ? '' : 'none';
      }});
    }}

    input.addEventListener('input', applyFilter);
    input.addEventListener('keydown', (e) => {{
      if (e.key === 'Enter') {{
        const v = normalize(input.value);
        const n = parseInt(v, 10);
        if (!Number.isNaN(n)) {{
          const el = document.getElementById('q' + n);
          if (el) el.scrollIntoView({{behavior: 'smooth', block: 'start'}});
        }}
      }}
    }});
  </script>
</body>
</html>
""",
        encoding="utf-8",
    )

    print(f"Wrote: {OUT_PATH}")


if __name__ == "__main__":
    main()

