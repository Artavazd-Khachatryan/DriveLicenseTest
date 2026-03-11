---
name: kmp-github-ci
description: Creates or updates GitHub Actions CI pipelines for Kotlin Multiplatform (KMP) repos with Android and iOS targets. Use when the user asks to run/build/check Android and then iOS in GitHub Actions, mentions "pipeline", "GitHub workflow", "CI", "multiplatform", "KMP", or needs platform-specific checks.
---

# KMP GitHub CI (Android + iOS)

## Quick start

When asked to add or update CI for this repo:

1. **Inspect repo shape**
   - Confirm Gradle wrapper exists: `./gradlew`
   - Identify Android app module (often `:androidApp` or `:app`)
   - Identify KMP shared module(s) (often `:composeApp`, `:shared`)
   - Identify iOS Xcode project/workspace (`iosApp/*.xcodeproj` or `*.xcworkspace`)

2. **Choose default checks**
   - **Android job (Linux)**: `./gradlew clean check :androidApp:assembleDebug`
   - **iOS job (macOS)**:
     - Build KMP framework (prefer simulator): `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`
     - Build & test with Xcode: `xcodebuild ... clean build test`

3. **Create workflow**
   - Prefer a single workflow with two jobs: Android first, iOS second (`needs: android`)
   - Use **JDK 17** and **Gradle cache**
   - Add `chmod +x gradlew` step

4. **Validate**
   - If Xcode scheme is unknown, run `xcodebuild -list -project <path>` and select a scheme that builds/tests
   - Ensure iOS destination matches a simulator available on `macos-latest`

## Repo-specific defaults (DriveLicenseTest)

If this repository matches the typical modules:

- Android app: `:androidApp`
- KMP shared: `:composeApp`
- iOS project: `iosApp/iosApp.xcodeproj`
- iOS scheme (likely): `iosApp`

Use the workflow template in `.github/workflows/mobile-ci.yml` unless the user requests separate workflows.

## Workflow template guidance

When generating or editing `.github/workflows/*.yml`:

- Trigger on:
  - `pull_request` and `push` to `main`/`master`
- Android:
  - `runs-on: ubuntu-latest`
  - `actions/setup-java@v4` with `temurin` and `java-version: 17`
  - `cache: gradle`
  - `./gradlew clean check :androidApp:assembleDebug --stacktrace`
- iOS:
  - `runs-on: macos-latest`
  - Build KMP framework first (simulator)
  - Use `xcodebuild` with:
    - `-project` or `-workspace`
    - `-scheme <scheme>`
    - `-destination 'platform=iOS Simulator,name=iPhone 15'` (adjust if needed)

## When it fails (common fixes)

- **Xcode scheme not found**: list schemes with `xcodebuild -list` and update `-scheme`.
- **Simulator not found**: change destination to a device available on the runner (e.g. `iPhone 14` / `iPhone 15`).
- **KMP framework task name differs**:
  - List tasks: `./gradlew :composeApp:tasks --all`
  - Use the `linkDebugFramework*` task for the simulator architecture you need.

## Additional resources

- Details and command snippets: see [reference.md](reference.md)

