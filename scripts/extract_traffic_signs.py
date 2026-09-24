#!/usr/bin/env python3
"""Crop official Armenian traffic signs from ARLIS figure sheets and store them.

Source of the pictures: government decision N 955-Ն, appendix 1, figures 2–9
on https://www.arlis.am/hy/acts/73067 (one JPEG per sign group).
Source of the names and meanings: the same appendix, Form N 1.

Each sign is cut out of its group sheet the way question figures are cut out
of the exam books: one drawable per sign, named sign_{code}.webp.
"""
from __future__ import annotations

import re
import sqlite3
import subprocess
from pathlib import Path

from PIL import Image, ImageFilter

REPO = Path(__file__).resolve().parents[1]
EXCERPT = Path(__file__).resolve().parent / "signs" / "official_rules_excerpt.txt"
SHEETS = Path(__file__).resolve().parent / "signs" / "sheets"
DRAWABLE = REPO / "ui/src/commonMain/composeResources/drawable"
DB_PATH = REPO / "database/src/commonMain/resources/license_test_questions.db"
REVIEW = REPO / "scripts/review/traffic-signs.html"

# Figure number on the official page -> category key used in the rules text.
FIGURES = {
    2: "warning",
    3: "priority",
    4: "prohibitory",
    5: "mandatory",
    6: "special",
    7: "information",
    8: "service",
    9: "plate",
}

CATEGORY_TITLES = {
    "warning": "Նախազգուշացնող նշաններ",
    "priority": "Առավելության նշաններ",
    "prohibitory": "Արգելող նշաններ",
    "mandatory": "Թելադրող նշաններ",
    "special": "Հատուկ թելադրանքի նշաններ",
    "information": "Տեղեկատվության նշաններ",
    "service": "Սպասարկման նշաններ",
    "plate": "Լրացուցիչ տեղեկատվության նշաններ",
}

HEADER_RE = re.compile(r"^(\d)\.\s+[Ա-Ֆ]")
CODE_RE = re.compile(r"\d+\.\d+(?:\.\d+)?")
REPEALED_RE = re.compile(r"ուժը կորցրել")
NAME_RE = re.compile(r"«([^»]+)»")


def expand_codes(text: str) -> list[str]:
    """Expand '1.4.1.-1.4.6' and '2.3.2 - 2.3.7' into individual codes, in order."""
    range_re = re.compile(
        r"(\d+\.\d+(?:\.\d+)?)\s*\.?\s*[-–]\s*(\d+\.\d+(?:\.\d+)?)"
    )
    spans = []
    for m in range_re.finditer(text):
        start, end = m.group(1), m.group(2)
        spans.append((m.start(), m.end(), _fill_range(start, end)))
    if not spans:
        return CODE_RE.findall(text)
    out: list[str] = []
    cursor = 0
    for start, end, codes in spans:
        out.extend(CODE_RE.findall(text[cursor:start]))
        out.extend(codes)
        cursor = end
    out.extend(CODE_RE.findall(text[cursor:]))
    # Drop codes that were only the range endpoints already expanded.
    return _dedupe_keep_order(out)


def _fill_range(start: str, end: str) -> list[str]:
    sp, ep = start.split("."), end.split(".")
    if sp[:-1] != ep[:-1]:
        return [start, end]
    prefix = ".".join(sp[:-1])
    a, b = int(sp[-1]), int(ep[-1])
    step = 1 if b >= a else -1
    return [f"{prefix}.{n}" for n in range(a, b + step, step)]


def _dedupe_keep_order(codes: list[str]) -> list[str]:
    seen = set()
    out = []
    for code in codes:
        if code not in seen:
            seen.add(code)
            out.append(code)
    return out


def _clean(text: str) -> str:
    text = text.replace("`", "՝")
    text = re.sub(r"\s*՝\s*«", " «", text)
    text = re.sub(r"\s+", " ", text).strip(" .;՝")
    return text


def parse_signs(path: Path) -> dict[str, list[dict]]:
    """Return {category: [{code, name, description}, ...]} in sheet order."""
    current = None
    grouped: dict[str, list[dict]] = {k: [] for k in CATEGORY_TITLES}
    intro = {k: "" for k in CATEGORY_TITLES}
    keys = list(CATEGORY_TITLES)
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("(") or line.startswith("|"):
            continue
        header = HEADER_RE.match(line)
        if header and "նկար" in line:
            idx = int(header.group(1)) - 1
            if 0 <= idx < len(keys):
                current = keys[idx]
            continue
        if current is None:
            continue
        if not CODE_RE.match(line):
            if not intro[current] and not line.startswith("Դրանք"):
                intro[current] = _clean(line)
            continue
        if REPEALED_RE.search(line) or "«" not in line:
            continue
        first_q = line.find("«")
        if re.search(r"[Ա-Ֆա-և]", line[:first_q]):
            continue
        end = re.search(r"»\s*\.", line)
        head = line[: end.start()] if end else line
        codes = expand_codes(head[: head.find("«")] if "«" in head else head)
        codes.extend(CODE_RE.findall(
            " ".join(re.findall(r"(\d+\.\d+(?:\.\d+)?)\s*[.՝`,]*\s*(?=«)", head))
        ))
        codes = [c for c in _dedupe_keep_order(codes) if c not in {s["code"] for s in grouped[current]}]
        if codes == ["4.8"]:
            codes = ["4.8.1", "4.8.2", "4.8.3"]
        if not codes:
            continue
        names = NAME_RE.findall(line)
        # One shared name, or one name per code when the line lists them.
        if len(names) == len(codes):
            name_for = dict(zip(codes, names))
        elif names:
            name_for = {code: names[0] for code in codes}
        else:
            name_for = {code: "" for code in codes}
        # Meaning is the sentence after the sign's own name, not a later quotation.
        closed = re.search(r"»\s*\.\s*", line)
        after = line[closed.end():] if closed else ""
        shared = _clean(CODE_RE.sub(" ", after))
        for code in codes:
            variant = ""
            m = re.search(
                re.escape(code) + r"\s*[՝`]\s*([^.:]+)",
                line,
            )
            if m and m.group(1).strip(" «»") not in names:
                variant = _clean(m.group(1))
            name = _clean(name_for.get(code) or (names[0] if names else code))
            description = shared or name
            if variant and variant not in description:
                description = f"{description} {code}՝ {variant}.".strip()
            grouped[current].append(
                {
                    "code": code,
                    "name": name,
                    "description": description,
                    "category": current,
                }
            )
    return grouped


def _signish(r: int, g: int, b: int) -> bool:
    mx, mn = max(r, g, b), min(r, g, b)
    if mx > 245 and mn > 235:
        return False
    if mx >= 70 and (mx - mn) > 35:
        return True
    if mx < 90 and mn < 80:
        return True
    return False


def detect_boxes(im: Image.Image) -> list[tuple[int, int, int, int]]:
    w, h = im.size
    px = im.load()
    seen = bytearray(w * h)
    raw: list[list[int]] = []
    for y in range(h):
        for x in range(w):
            i = y * w + x
            if seen[i]:
                continue
            r, g, b = px[x, y][:3]
            if not _signish(r, g, b):
                seen[i] = 1
                continue
            stack = [(x, y)]
            seen[i] = 1
            minx = maxx = x
            miny = maxy = y
            count = 0
            while stack:
                cx, cy = stack.pop()
                count += 1
                minx, maxx = min(minx, cx), max(maxx, cx)
                miny, maxy = min(miny, cy), max(maxy, cy)
                for dx in (-1, 0, 1):
                    for dy in (-1, 0, 1):
                        if dx == 0 and dy == 0:
                            continue
                        nx, ny = cx + dx, cy + dy
                        if nx < 0 or ny < 0 or nx >= w or ny >= h:
                            continue
                        j = ny * w + nx
                        if seen[j]:
                            continue
                        rr, gg, bb = px[nx, ny][:3]
                        if not _signish(rr, gg, bb):
                            seen[j] = 1
                            continue
                        seen[j] = 1
                        stack.append((nx, ny))
            bw, bh = maxx - minx + 1, maxy - miny + 1
            if count >= 80 and 16 <= bw <= 180 and 16 <= bh <= 130:
                raw.append([minx, miny, maxx, maxy])
    keep = []
    for a in raw:
        cx, cy = (a[0] + a[2]) / 2, (a[1] + a[3]) / 2
        area = (a[2] - a[0]) * (a[3] - a[1])
        inside = False
        for b in raw:
            if b is a:
                continue
            if (b[2] - b[0]) * (b[3] - b[1]) <= area:
                continue
            if b[0] - 2 <= cx <= b[2] + 2 and b[1] - 2 <= cy <= b[3] + 2:
                inside = True
                break
        if not inside:
            keep.append(tuple(a))
    keep.sort(key=lambda b: (b[1], b[0]))
    return keep


def _same_row(a, b) -> bool:
    ac = (a[1] + a[3]) / 2
    bc = (b[1] + b[3]) / 2
    return abs(ac - bc) < 28


def _pair_gap(a, b) -> int | None:
    acy, bcy = (a[1] + a[3]) / 2, (b[1] + b[3]) / 2
    acx, bcx = (a[0] + a[2]) / 2, (b[0] + b[2]) / 2
    if abs(acy - bcy) < 32:
        if a[2] <= b[0]:
            return b[0] - a[2]
        if b[2] <= a[0]:
            return a[0] - b[2]
        return 0
    if abs(acx - bcx) < 24:
        if a[3] <= b[1]:
            return b[1] - a[3]
        if b[3] <= a[1]:
            return a[1] - b[3]
        return 0
    return None


def merge_fragments(boxes: list[tuple[int, int, int, int]], target: int):
    """Join split pieces (chevrons, double crosses, multi-panel signs)."""
    boxes = [list(b) for b in boxes if (b[2] - b[0]) >= 24 or (b[3] - b[1]) >= 28]
    while len(boxes) > target:
        best = None
        best_gap = 999
        for i in range(len(boxes)):
            for j in range(i + 1, len(boxes)):
                gap = _pair_gap(boxes[i], boxes[j])
                ah, bh = boxes[i][3] - boxes[i][1], boxes[j][3] - boxes[j][1]
                limit = 18 if ah <= 32 and bh <= 32 else 4
                if gap is not None and gap < best_gap and gap <= limit:
                    best, best_gap = (i, j), gap
        if best is None:
            break
        i, j = best
        a, b = boxes[i], boxes[j]
        merged = [min(a[0], b[0]), min(a[1], b[1]), max(a[2], b[2]), max(a[3], b[3])]
        boxes = [box for k, box in enumerate(boxes) if k not in best] + [merged]
    return _row_major(boxes)


def _row_major(boxes: list[list[int]]) -> list[tuple[int, int, int, int]]:
    """Reading order: each horizontal row left to right, rows top to bottom."""
    ordered = sorted(boxes, key=lambda b: (b[1] + b[3]) / 2)
    rows: list[list] = []
    for b in ordered:
        cy = (b[1] + b[3]) / 2
        if not rows or abs(cy - rows[-1][0]) > 40:
            rows.append([cy, [b]])
        else:
            rows[-1][1].append(b)
            rows[-1][0] = sum((x[1] + x[3]) / 2 for x in rows[-1][1]) / len(rows[-1][1])
    out = []
    for _, row in rows:
        row.sort(key=lambda b: b[0])
        out.extend(tuple(b) for b in row)
    return out


def _union(parts: list[tuple[int, int, int, int]]) -> tuple[int, int, int, int]:
    return (
        min(p[0] for p in parts),
        min(p[1] for p in parts),
        max(p[2] for p in parts),
        max(p[3] for p in parts),
    )


def _take_groups(boxes: list[tuple[int, int, int, int]], sizes: list[int]):
    if sum(sizes) != len(boxes):
        raise SystemExit(f"group sizes {sum(sizes)} != {len(boxes)} boxes")
    out = []
    i = 0
    for size in sizes:
        out.append(_union(boxes[i:i + size]))
        i += size
    return out


# Several drawings on the sheet are examples of one code. They are stored as one image.
FIG6_GROUP_SIZES = (
    [1] * 17
    + [7]          # 5.15.2 lane arrows
    + [2, 3]       # 5.15.3, 5.15.4
    + [1, 1, 3]    # 5.15.5, 5.15.6, 5.15.7
    + [1] * 23
)


def _stack_fragments(boxes: list[list[int]]) -> list[tuple[int, int, int, int]]:
    """Join stacked boards and broken text of one sign. Leave neighboring signs apart."""
    def gx(a, b):
        if a[2] < b[0]:
            return b[0] - a[2]
        if b[2] < a[0]:
            return a[0] - b[2]
        return 0

    def gy(a, b):
        if a[3] < b[1]:
            return b[1] - a[3]
        if b[3] < a[1]:
            return a[1] - b[3]
        return 0

    changed = True
    while changed:
        changed = False
        for i in range(len(boxes)):
            for j in range(i + 1, len(boxes)):
                a, b = boxes[i], boxes[j]
                aw, ah = a[2] - a[0], a[3] - a[1]
                bw, bh = b[2] - b[0], b[3] - b[1]
                ox = min(a[2], b[2]) - max(a[0], b[0])
                oy = min(a[3], b[3]) - max(a[1], b[1])
                stack = ox > 20 and 0 <= gy(a, b) <= 16
                frag = min(aw, bw) < 45 or min(ah, bh) < 22
                side = oy > 10 and 0 <= gx(a, b) <= 8 and frag
                if stack or side:
                    boxes[i] = [min(a[0], b[0]), min(a[1], b[1]), max(a[2], b[2]), max(a[3], b[3])]
                    del boxes[j]
                    changed = True
                    break
            if changed:
                break
    return _row_major(boxes)


def figure6_boxes(im: Image.Image):
    boxes = _row_major([list(b) for b in detect_boxes(im)])
    return _take_groups(boxes, FIG6_GROUP_SIZES)


def figure7_boxes(im: Image.Image):
    raw = [list(b) for b in detect_boxes(im)]
    boxes = _stack_fragments([b[:] for b in raw])
    if len(boxes) != 39:
        raise SystemExit(f"figure 7 expected 39 panels after stacking, got {len(boxes)}")
    truck = boxes[27]
    trucks = [
        tuple(b) for b in raw
        if truck[0] - 2 <= b[0] and b[2] <= truck[2] + 2
        and truck[1] - 2 <= b[1] and b[3] <= truck[3] + 2
    ]
    trucks.sort(key=lambda b: (b[1], b[0]))
    # The three truck boards sit side by side; drop pieces of a board already inside another.
    trucks = [b for b in trucks if not any(
        o is not b and o[0] <= b[0] and b[2] <= o[2] and o[1] <= b[1] and b[3] <= o[3] and (o[2] - o[0]) * (o[3] - o[1]) > (b[2] - b[0]) * (b[3] - b[1])
        for o in trucks
    )]
    if len(trucks) != 3:
        raise SystemExit(f"figure 7 truck row split into {len(trucks)} pieces")

    def one(i):
        return boxes[i]

    return [
        *map(one, range(10)),             # 6.1 .. 6.8.2
        one(12),                           # 6.8.3, printed under 6.8.1
        _union([boxes[i] for i in (10, 11, 13, 14, 15)]),  # 6.9.1 examples
        one(16),                           # 6.9.2
        one(17),                           # 6.9.3
        one(18),                           # 6.10.1
        _union([boxes[i] for i in (19, 20, 21)]),          # 6.10.2 examples
        *map(one, range(22, 27)),          # 6.11 .. 6.14.2
        *trucks,                           # 6.15.1 .. 6.15.3
        one(28), one(29),                  # 6.16, 6.17
        one(30), one(31), one(34),         # 6.18.1 .. 6.18.3
        one(32), one(33),                  # 6.19.1, 6.19.2
        *map(one, range(35, 39)),          # 6.20.1 .. 6.21.2
    ]


def crop(im: Image.Image, box, pad=4) -> Image.Image:
    x0, y0, x1, y1 = box
    x0, y0 = max(0, x0 - pad), max(0, y0 - pad)
    x1, y1 = min(im.width - 1, x1 + pad), min(im.height - 1, y1 + pad)
    tile = im.crop((x0, y0, x1 + 1, y1 + 1))
    # The official sheets are about 500px wide, so each sign is tiny.
    # Lanczos keeps the edges smooth; a light unsharp pass puts the stroke back.
    scale = 8
    tile = tile.resize((tile.width * scale, tile.height * scale), Image.Resampling.LANCZOS)
    return tile.filter(ImageFilter.UnsharpMask(radius=1.2, percent=140, threshold=2))


def image_name(code: str) -> str:
    return "sign_" + code.replace(".", "_")


def save_webp(im: Image.Image, path: Path) -> None:
    png = path.with_suffix(".png")
    im.save(png)
    subprocess.run(
        ["cwebp", "-q", "92", "-m", "6", "-sharp_yuv", "-quiet", str(png), "-o", str(path)],
        check=True,
    )
    png.unlink()


def write_review(rows: list[dict]) -> None:
    REVIEW.parent.mkdir(parents=True, exist_ok=True)
    cards = []
    for row in rows:
        rel = Path("..") / ".." / "ui/src/commonMain/composeResources/drawable" / row["image"]
        cards.append(
            f"""<figure>
  <img src="{rel.as_posix()}" alt="{row['code']}">
  <figcaption><b>{row['code']}</b> {row['name']}<br>{row['description']}</figcaption>
</figure>"""
        )
    REVIEW.write_text(
        """<!doctype html><meta charset="utf-8"><title>Traffic signs</title>
<style>
body{font-family:system-ui,sans-serif;background:#f4f4f5;margin:24px}
.grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(220px,1fr));gap:16px}
figure{background:#fff;border-radius:12px;padding:12px;margin:0}
img{width:100%;height:140px;object-fit:contain;background:#fff}
figcaption{font-size:13px;line-height:1.35}
h2{margin:28px 0 12px}
</style>
<h1>Official traffic signs</h1>
"""
        + "\n".join(cards),
        encoding="utf-8",
    )


def load_db(rows: list[dict]) -> None:
    conn = sqlite3.connect(DB_PATH)
    conn.execute(
        """
        CREATE TABLE IF NOT EXISTS TrafficSign (
            id INTEGER NOT NULL PRIMARY KEY,
            code TEXT NOT NULL UNIQUE,
            category TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            image TEXT
        )
        """
    )
    conn.execute("DELETE FROM TrafficSign")
    conn.executemany(
        "INSERT INTO TrafficSign (id, code, category, name, description, image) VALUES (?, ?, ?, ?, ?, ?)",
        [
            (i, r["code"], r["category"], r["name"], r["description"], r["image"])
            for i, r in enumerate(rows, start=1)
        ],
    )
    conn.commit()
    conn.close()


def main() -> None:
    grouped = parse_signs(EXCERPT)
    for key, signs in grouped.items():
        print(f"parsed {key:12} {len(signs):3}  {signs[0]['code'] if signs else '-'} .. {signs[-1]['code'] if signs else '-'}")

    saved: list[dict] = []
    for old in DRAWABLE.glob("sign_*.webp"):
        old.unlink()
    for fig, category in FIGURES.items():
        sheet = SHEETS / f"nkar{fig}.jpg"
        signs = grouped[category]
        im = Image.open(sheet).convert("RGB")
        if fig == 6:
            boxes = figure6_boxes(im)
        elif fig == 7:
            boxes = figure7_boxes(im)
        else:
            boxes = merge_fragments(detect_boxes(im), len(signs))
        print(f"sheet {fig} {category:12} codes={len(signs):3} boxes={len(boxes):3}")
        if len(boxes) != len(signs):
            raise SystemExit(f"figure {fig}: {len(boxes)} boxes vs {len(signs)} codes")
        for sign, box in zip(signs, boxes):
            name = image_name(sign["code"]) + ".webp"
            save_webp(crop(im, box), DRAWABLE / name)
            saved.append({**sign, "image": name})

    load_db(saved)
    write_review(saved)
    print(f"saved {len(saved)} signs")


if __name__ == "__main__":
    main()
