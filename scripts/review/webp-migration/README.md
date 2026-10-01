# WebP migration quality review

Generate the comparison gallery locally (do not commit it):

```bash
python3 scripts/migrate_question_images_to_webp.py
open scripts/review/webp-migration/quality_review.html
```

Each card shows **original PNG** (left) vs **WebP q85 at app display width** (right), with file sizes.

`originals/` holds PNG backups for side-by-side review only — do not commit (≈270 MB).
`quality_review.html` is the generated gallery — also gitignored.

Migration script: `scripts/migrate_question_images_to_webp.py`
