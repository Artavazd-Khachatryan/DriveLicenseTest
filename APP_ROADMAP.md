# APP_ROADMAP — Driving License Test App

> Living document. Update status after completing each step.

---

## Phase 1: Wire Real Data & Fix Foundations

The app has DB models and UI but they aren't connected. Fix this first — everything else builds on it.

- **1.1 Persist test results** — Save `TestSession` and `QuestionAttempt` to database when a test completes
- **1.2 Track question progress** — Update `QuestionWithProgress` (timesAnswered, timesCorrect, timesIncorrect) after each answer
- **1.3 Real stats on HomeScreen** — Replace hardcoded 70% with actual accuracy from DB; show real total questions answered, tests taken
- **1.4 Fix image loading** — Add fallback UI (placeholder icon) when question image is missing; handle errors gracefully instead of silent console logs

---

## Phase 2: Stats Screen & Review Mistakes

Users need to see their progress and learn from errors.

- **2.1 Build StatsScreen** — Overall accuracy ring, total attempts, correct/incorrect counts
- **2.2 Category breakdown** — Per-category accuracy with progress bars
- **2.4 Test history list** — Show past test sessions (date, score, pass/fail icon)
- **2.5 Review Mistakes screen** — Lists incorrectly answered questions with correct answer shown

---

## Phase 3: Practice Mode & Smart Selection

Move beyond random 20-question tests.

- **3.1 Practice by category** — Category picker screen with question counts; starts filtered test
- **3.3 Weak areas practice** — Pulls questions with most mistakes first via getWeakAreaQuestions()
- **3.4 Configurable test length** — Let user choose 10/20/30 questions before starting
- **3.5 Exam simulation mode** — 30 questions, 20-minute countdown timer, auto-submits on timeout

---

## Phase 4: Navigation & UX Polish

Replace fragile navigation and improve overall feel.

- **4.1 Type-safe navigation** — Sealed class `Screen` with back stack (`mutableStateListOf`)
- **4.2 Bottom navigation bar** — Home / Practice / Stats tabs via `AppBottomBar`, shown on top-level screens only
- **4.3 Transitions** — `AnimatedContent`: slide for stack push/pop, fade for tab switches
- **4.4 Back handling** — Exit confirmation dialog when tapping back during an active test
- **4.5 Use existing components** — `StatChip` wired into HomeScreen progress section

---

## Phase 5: Armenian Localization

The app targets Armenian users preparing for the driving exam.

- **5.1 Extract all strings** — Move hardcoded English text to string resources
- **5.2 Armenian translations** — Add Armenian (`hy`) translations for all UI strings
- **5.3 Armenian as default** — Set Armenian as primary language, English as fallback

---

## Phase 6: UI Refinements

Polish the look and feel.

- **6.1 Dark mode audit** — Fixed hardcoded Color.Red in timer; all other colors use colorScheme tokens
- **6.2 Long text handling** — Existing verticalScroll on detail screens handles long content; Text wraps naturally
- **6.3 Empty states** — Mistakes screen has illustrated empty card; Stats cards show inline empty messages
- **6.4 Loading states** — CircularProgressIndicator added to StatsScreen and ReviewMistakesScreen
- **6.5 Accessibility pass** — ProgressRing contentDescription; AnswerButton incorrect color fixed; home ring CD wired
- [x] **6.6 Tablet/landscape** — `AdaptiveContentContainer` (600dp+ expanded); Home start/progress side-by-side; question image beside text on wide screens; 720dp max width
- [x] **6.7 Motivating messages** — Context-aware encouragement on Home (streak, accuracy, first visit) and test results (score bands)
- [x] **6.8 UI polish (May 2026)** — Armenian string grammar, Material icons instead of emoji, staggered home animations, dynamic test-length subtitle, category string resources

---

## Phase 7: Crash Reporting (Firebase Crashlytics)

Capture crashes and non-fatal errors on **Android** and **iOS** so production issues are visible before users report them.

**Recommended stack:** [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics) (native SDK per platform; KMP bridge via `expect`/`actual`).

- **7.1 Firebase project & apps** — Create Firebase project; register `androidApp` (package `com.drive.license.test`) and iOS app (`com.drive.license.test.DriveLicenseTest`); download `google-services.json` and `GoogleService-Info.plist` (store secrets outside git or use CI secrets).
- **7.2 Android Crashlytics** — Apply Google Services + Crashlytics Gradle plugins in `androidApp`; depend on Firebase BOM; enable collection in `LicenseApplication` / `MainActivity`; verify debug mapping upload for release builds.
- **7.3 iOS Crashlytics** — Add Firebase iOS SDK (SPM or CocoaPods) to `iosApp`; include `GoogleService-Info.plist` in target; run upload-symbols run script for dSYM; confirm crashes appear in Firebase console from a test crash on simulator/device.
- **7.4 KMP crash reporting API** — `expect object CrashReporter` in `composeApp` or `domain` with `logNonFatal(throwable, message?)`, `log(message)`, `setUserId`, `setCustomKey`; `actual` implementations delegate to Firebase on each platform; no-op in common tests.
- **7.5 Wire critical paths** — Report non-fatals for: DB init/load failures, question image resolve failures, AI/network errors (when enabled), uncaught coroutine exceptions in `MainScreen` / repositories (via `CoroutineExceptionHandler`).
- **7.6 Privacy & build flavors** — Document data collected; disable or sample Crashlytics in debug if desired; ensure release builds upload ProGuard/R8 mapping (Android) and dSYM (iOS).
- **7.7 Verification checklist** — Force test crash on Android + iOS; confirm event in Firebase within ~5 min; confirm symbolicated stack traces for release build.

**Out of scope for 7.x (optional later):** Analytics, Performance Monitoring, Remote Config, Sentry (unless we switch providers).

---

## Status Log


| Date       | What was done                                                                                                                   |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------- |
| 2026-04-21 | Initial roadmap created                                                                                                         |
| 2026-04-21 | Phase 1 complete — DB persistence wired, real stats on HomeScreen, image fallback                                               |
| 2026-04-21 | Phase 2 complete — StatsScreen (accuracy, categories, history), ReviewMistakesScreen |
| 2026-04-28 | Phase 3 (partial) — Category picker, weak areas practice, exam simulation with 20-min countdown timer                           |
| 2026-04-28 | Phase 4 complete — Sealed class navigation, back stack, bottom nav bar, slide/fade transitions, exit dialog                     |
| 2026-04-28 | Phase 5 complete — All strings in Armenian resources as default language |
| 2026-04-28 | Phase 6 (5/6 done) — Dark mode, loading states, empty states, accessibility, long text; 6.6 tablet/landscape still to verify    |
| 2026-04-28 | Phase 3.4 done — Configurable test length (10/20/30 chip selector on HomeScreen)                                                |
| 2026-05-20 | Phase 7 removed from roadmap (stretch goals out of scope); motivating messages added as 6.7 instead of achievement badges       |
| 2026-05-26 | Phase 6.6–6.8 — Adaptive tablet layouts, string grammar pass, home animations, `MotivationLogic` unit tests                     |
| 2026-05-26 | Removed from scope: difficulty distribution (2.3), practice by book (3.2), RTL/font check (5.4)                               |
| 2026-05-26 | Phase 7 added — Firebase Crashlytics plan for Android + iOS with KMP `expect`/`actual` bridge                                   |
