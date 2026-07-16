# WebP migration quality review

Open the comparison gallery in a browser:

```bash
open scripts/review/webp-migration/quality_review.html
```

Each card shows **original PNG** (left) vs **WebP q85 at app display width** (right), with file sizes.

`originals/` holds PNG backups for side-by-side review only — do not commit (≈270 MB).

Migration script: `scripts/migrate_question_images_to_webp.py`
