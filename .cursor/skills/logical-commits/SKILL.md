---
name: logical-commits
description: >-
  Splits work into small, reviewable git commits with one clear purpose each.
  Use when the user asks for separate commits, logical commit history, reviewable
  PRs, or says each change should be understandable on its own.
---

# Logical Commits

**Goal:** Each commit should be logical, reviewable, and understandable on its own.

A reviewer (or future you) should read one commit message and one diff and know exactly what changed and why — without needing the rest of the branch.

## When to split

Split when a working tree contains **multiple independent concerns**, for example:

- New feature + navigation restructure
- Bug fix + unrelated string tweaks
- Data layer + UI polish for a different screen

Keep together when changes are **tightly coupled** and useless alone (e.g. SQL query + repository method + screen that calls it for the same feature).

## Commit sizing rules

1. **One user-visible intent per commit** — not one file per commit.
2. **Each commit must build** — run the relevant Gradle/Xcode check before committing.
3. **Message explains why** — 1–2 sentences; imperative mood.
4. **Avoid mixed files** — if `MainScreen.kt` or `strings.xml` spans features, stage only the hunks for the current commit (reset file to HEAD, re-apply slice, commit, repeat).
5. **Order dependencies** — commit foundation first (models → DB → repo → UI → navigation).

## Workflow

```text
1. git status + git diff — list concerns, not files
2. Pick the smallest independent concern
3. Stage only files/hunks for that concern
4. Build
5. Commit with a focused message
6. Repeat until working tree is clean
```

### Handling mixed files

When one file contains multiple features:

1. Backup the full WIP file (`cp file /tmp/file.wip`).
2. `git checkout HEAD -- file` to reset.
3. Re-apply only the changes for the current commit.
4. Stage, build, commit.
5. For the next commit, continue from HEAD or restore from backup and layer the next slice.

Never batch unrelated concerns into one commit because staging is inconvenient.

## Message format

```text
<Short imperative summary>.

<Optional one sentence: user benefit or behavior change.>
```

**Good**

- `Add weak areas review screen and full-pool practice.`
- `Move mistakes and weak areas from home to practice tab.`
- `Run color vision plates before exam simulation theory test.`

**Avoid**

- `WIP`, `fixes`, `updates`, `misc changes`
- Messages that list file names instead of behavior

## Checklist before each commit

- [ ] Single clear purpose
- [ ] Diff matches the message (no drive-by edits)
- [ ] Project builds
- [ ] No secrets or generated noise
- [ ] Later commits do not depend on uncommitted work from earlier steps

## Example split (this repo)

| Commit | Scope |
|--------|--------|
| 1 | Weak areas data + list screen + practice navigation |
| 2 | Home simplified; practice tab gets review cards |
| 3 | Exam simulation color-vision prelude |

Each is reviewable alone; together they form the full feature.
