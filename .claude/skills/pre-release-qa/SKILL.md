---
name: pre-release-qa
description: Pre-release QA for the Driving License Test app. Use when the user asks for "pre-release QA", a "release check", to "verify before release", or to "test new features before release". Runs content and unit checks, the build and Compose smoke tests, then walks docs/release-qa-checklist.md on an emulator and writes a verdict report.
argument-hint: "[quick | full]"
---

# Pre-release QA

Argument: `quick` = steps a and b (with `--skip-content --no-device`), then a device run of only the changed features. `full` (default) = everything.

Model note: run the heavy steps through subagents (`device-tester`, `general-purpose`) and keep the parent session to coordination.

## a. Scope since the last release

1. `TAG=$(git describe --tags --abbrev=0)` (tags look like `v1.1.0.14`). Run `git log --oneline $TAG..HEAD` and `git diff --name-only $TAG..HEAD`.
2. Map changed files under `ui/src/commonMain/kotlin/com/drive/license/test/ui/` to features using the sections of `docs/release-qa-checklist.md` (for example `crossing/` -> section 12, `SettingsScreen.kt` -> 13, `StatsScreen.kt` and `TestSession*Review*` -> 7, `ColorVision*` -> 9 and 10, `TrafficSignsScreen.kt` -> 11, `Bookmarks*` -> 6, `Practice*`/`CategoryPicker*` -> 8, `MainScreen.kt`/`TestSession.kt`/`Screen.kt` -> 3 and 9, `theme/` -> 14 and 16, `components/` -> all).
3. Also note changes to `database/`, `ContentRefresh`, `database/src/commonMain/resources/license_test_questions.db` (then section 15 is mandatory) and `scripts/`.
4. For every changed or new screen file, confirm the checklist has a section that covers it. If not, STOP: read the screen code and `ui/src/commonMain/composeResources/values/strings.xml`, add a real section (entry point, Armenian labels from strings.xml, 3-8 steps, expected result, "Automated by"), and commit it before continuing.

## b. Automated checks

1. Run `scripts/release_check.sh` (add `--no-device` if the emulator is used by someone else). If the official PDFs are unavailable or fetching times out, use `--skip-content` and pre-download:
   `for p in abc dt; do mkdir -p /tmp/official-exam-2026/$p; for n in 1 2 3 4 5 6 7 8 9 10; do curl -fL --max-time 120 -o /tmp/official-exam-2026/$p/$n.pdf https://roadpolice.am/exam/$p/hy/$n.pdf; done; done`
2. `quick`: run it with `--skip-content --no-device`.
3. Any failure is a release blocker. Report it and do not start device walkthroughs until it is fixed.

## c. On-device walkthrough (skip for `quick` except the changed features)

1. Check `adb devices`. If none is attached, list AVDs with `$ANDROID_HOME/emulator/emulator -list-avds` and boot one (phone size 1080x2340 @420dpi preferred). Do not use an emulator another session is driving.
2. Install the debug APK (`./gradlew :androidApp:installDebug`, or `adb install -r` on the built APK). Never clear app data unless a checklist step says so.
3. Delegate to the `device-tester` agent type in batches of related sections (for example: 1-3; 4-8; 9-12; 13-14; 15-16). Give each agent the exact checklist text of its sections, the screenshot directory `scripts/review/release-qa-<version>-shots/`, and these quirks:
   - On the Pixel 10 Pro Fold AVD, `screencap` needs `-d <display id>`; get ids from `adb shell dumpsys SurfaceFlinger --display-id`.
   - Use `uiautomator dump` to get element bounds before tapping.
   - Never clear app data unless the step says so.
4. Require from each agent: per-step PASS/FAIL, screenshot filenames, and a final logcat scan for `FATAL`, `AndroidRuntime`, `SQLiteException`, `ANR`.
5. Order: sections for features changed since the tag first. Always run section 15 (manual variant) when the bundled DB or `ContentRefresh` changed. In `quick`, run only the changed sections.

## d. Report

Write `scripts/review/release-qa-<version>.md`, version from `scripts/print_app_version.sh`. Contents: build and version, commit range (`$TAG..HEAD`), automated results, per-feature PASS/FAIL table (all checklist sections, mark not-run as SKIPPED with a reason), anomalies each with severity (blocker / should-fix / note), screenshots path. The file is git-ignored (`scripts/review/release-qa-*.md` in `.gitignore`); do not commit it.

## e. Final answer

Short verdict (ready / not ready), blockers first, then should-fix, then notes. Never call it ready if any blocker or any FAIL on a changed feature remains, or if step b failed.
