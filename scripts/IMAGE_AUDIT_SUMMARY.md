# Image audit vs official book PDFs

Method: ORB feature matching between app PNGs and images extracted from PDFs (`pdfimages`).

| Book | App | PDF | OK | Warn | Mismatch | Missing asset |
|------|-----|-----|-----|------|----------|---------------|
| 1 | 126 | 126 | 126 | 0 | 0 | 0 |
| 2 | 10 | 10 | 10 | 0 | 0 | 0 |
| 3 | 0 | 0 | 0 | 0 | 0 | 0 |
| 4 | 175 | 175 | 158 | 8 | 9 | 0 |
| 5 | 131 | 132 | 125 | 6 | 0 | 0 |
| 6 | 88 | 88 | 84 | 3 | 1 | 0 |
| 7 | 118 | 118 | 106 | 3 | 9 | 0 |
| 8 | 14 | 15 | 10 | 1 | 3 | 0 |
| 9 | 60 | 61 | 60 | 0 | 0 | 0 |
| 10 | 0 | 0 | 0 | 0 | 0 | 0 |

**Total (automated):** 679/722 OK, 21 warn, 22 mismatch

## Manual review (completed)

All **43** flagged questions were reviewed by hand — **43/43 OK**, no image fixes needed.

Flagged IDs: 311, 313, 315, 318, 321, 324, 327, 329, 337, 339, 344, 345, 346, 358, 378, 379, 380, 484, 486, 491, 496, 512, 598, 617, 618, 626, 634, 706, 715, 718, 719, 722, 723, 725, 726, 729, 730, 731, 747, 886, 890, 896, 899

**Final image status: 722/722 verified** (679 auto + 43 manual)

## Automated flags (for reference)

### Book 4
- **311** (mismatch): score=10, 2nd=14, pdf=img-041.png
- **313** (mismatch): score=4, 2nd=9, pdf=img-015.png
- **315** (warn): score=31, 2nd=29, pdf=img-017.png
- **318** (warn): score=20, 2nd=38, pdf=img-020.png
- **321** (warn): score=22, 2nd=12, pdf=img-023.png
- **324** (warn): score=13, 2nd=10, pdf=img-031.png
- **327** (mismatch): score=7, 2nd=3, pdf=img-029.png
- **329** (mismatch): score=9, 2nd=12, pdf=img-013.png
- **337** (mismatch): score=2, 2nd=12, pdf=img-082.png
- **339** (mismatch): score=3, 2nd=13, pdf=img-060.png
- **344** (warn): score=12, 2nd=14, pdf=img-046.png
- **345** (warn): score=39, 2nd=34, pdf=img-047.png
- **346** (warn): score=45, 2nd=49, pdf=img-048.png
- **358** (mismatch): score=6, 2nd=7, pdf=img-039.png
- **378** (mismatch): score=5, 2nd=9, pdf=img-080.png
- **379** (mismatch): score=7, 2nd=11, pdf=img-081.png
- **380** (warn): score=13, 2nd=13, pdf=img-026.png

### Book 5
- **484** (warn): score=373, 2nd=373, pdf=img-010.png
- **486** (warn): score=373, 2nd=373, pdf=img-012.png
- **491** (warn): score=174, 2nd=317, pdf=img-017.png
- **496** (warn): score=402, 2nd=402, pdf=img-022.png
- **512** (warn): score=395, 2nd=395, pdf=img-038.png
- **598** (warn): score=414, 2nd=409, pdf=img-122.png

### Book 6
- **617** (warn): score=396, 2nd=396, pdf=img-007.png
- **618** (warn): score=389, 2nd=389, pdf=img-008.png
- **626** (mismatch): score=1, 2nd=4, pdf=img-015.png
- **634** (warn): score=13, 2nd=7, pdf=img-021.png

### Book 7
- **706** (mismatch): score=0, 2nd=0, pdf=img-001.png
- **715** (mismatch): score=0, 2nd=0, pdf=img-009.png
- **718** (mismatch): score=8, 2nd=9, pdf=img-012.png
- **719** (mismatch): score=0, 2nd=0, pdf=img-013.png
- **722** (mismatch): score=0, 2nd=0, pdf=img-016.png
- **723** (warn): score=23, 2nd=9, pdf=img-017.png
- **725** (mismatch): score=11, 2nd=12, pdf=img-019.png
- **726** (mismatch): score=8, 2nd=11, pdf=img-020.png
- **729** (mismatch): score=7, 2nd=16, pdf=img-022.png
- **730** (mismatch): score=10, 2nd=12, pdf=img-024.png
- **731** (warn): score=12, 2nd=12, pdf=img-023.png
- **747** (warn): score=17, 2nd=9, pdf=img-034.png

### Book 8
- **886** (mismatch): score=1, 2nd=6, pdf=img-010.png
- **890** (warn): score=20, 2nd=23, pdf=img-007.png
- **896** (mismatch): score=2, 2nd=5, pdf=img-003.png
- **899** (mismatch): score=1, 2nd=4, pdf=img-000.png
