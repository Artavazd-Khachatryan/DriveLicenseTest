# APP_ROADMAP — Driving License Test App

> Living document. Update status after completing each step.

---

## Phase 1: Wire Real Data & Fix Foundations

The app has DB models and UI but they aren't connected. Fix this first — everything else builds on it.

- [x] **1.1 Persist test results** — Save `TestSession` and `QuestionAttempt` to database when a test completes
- [x] **1.2 Track question progress** — Update `QuestionWithProgress` (timesAnswered, timesCorrect, timesIncorrect) after each answer
- [x] **1.3 Real stats on HomeScreen** — Replace hardcoded 70% with actual accuracy from DB; show real total questions answered, tests taken
- [x] **1.4 Fix image loading** — Add fallback UI (placeholder icon) when question image is missing; handle errors gracefully instead of silent console logs

---

## Phase 2: Stats Screen & Review Mistakes

Users need to see their progress and learn from errors.

- [x] **2.1 Build StatsScreen** — Overall accuracy ring, total attempts, correct/incorrect counts
- [x] **2.2 Category breakdown** — Per-category accuracy with progress bars
- [ ] **2.3 Difficulty distribution** — Show how many questions are EASY/MEDIUM/HARD/LEARNED based on `QuestionWithProgress.difficulty`
- [x] **2.4 Test history list** — Show past test sessions (date, score, pass/fail icon)
- [x] **2.5 Review Mistakes screen** — Lists incorrectly answered questions with correct answer shown

---

## Phase 3: Practice Mode & Smart Selection

Move beyond random 20-question tests.

- [x] **3.1 Practice by category** — Category picker screen with question counts; starts filtered test
- [ ] **3.2 Practice by book** — Book picker (Book 1–10), then questions from that book
- [x] **3.3 Weak areas practice** — Pulls questions with most mistakes first via getWeakAreaQuestions()
- [x] **3.4 Configurable test length** — Let user choose 10/20/30 questions before starting
- [x] **3.5 Exam simulation mode** — 30 questions, 20-minute countdown timer, auto-submits on timeout

---

## Phase 4: Navigation & UX Polish

Replace fragile navigation and improve overall feel.

- [x] **4.1 Type-safe navigation** — Sealed class `Screen` with back stack (`mutableStateListOf`)
- [x] **4.2 Bottom navigation bar** — Home / Practice / Stats tabs via `AppBottomBar`, shown on top-level screens only
- [x] **4.3 Transitions** — `AnimatedContent`: slide for stack push/pop, fade for tab switches
- [x] **4.4 Back handling** — Exit confirmation dialog when tapping back during an active test
- [x] **4.5 Use existing components** — `StatChip` wired into HomeScreen progress section

---

## Phase 5: Armenian Localization

The app targets Armenian users preparing for the driving exam.

- [x] **5.1 Extract all strings** — Move hardcoded English text to string resources
- [x] **5.2 Armenian translations** — Add Armenian (`hy`) translations for all UI strings
- [x] **5.3 Armenian as default** — Set Armenian as primary language, English as fallback
- [ ] **5.4 RTL/font check** — Verify Armenian script renders correctly on both platforms

---

## Phase 6: UI Refinements

Polish the look and feel.

- [x] **6.1 Dark mode audit** — Fixed hardcoded Color.Red in timer; all other colors use colorScheme tokens
- [x] **6.2 Long text handling** — Existing verticalScroll on detail screens handles long content; Text wraps naturally
- [x] **6.3 Empty states** — Mistakes screen has illustrated empty card; Stats cards show inline empty messages
- [x] **6.4 Loading states** — CircularProgressIndicator added to StatsScreen and ReviewMistakesScreen
- [x] **6.5 Accessibility pass** — ProgressRing contentDescription; AnswerButton incorrect color fixed; home ring CD wired
- [ ] **6.6 Tablet/landscape** — Verify 720dp max-width works, test side-by-side layout on tablets

---

## Phase 7: Stretch Goals

Nice-to-have features if time allows.

- [ ] **7.1 Achievement badges** — Badges for milestones (first test, 10 tests, 100% score, category mastery)
- [x] **7.2 Daily streak tracker** — Track consecutive days of practice
- [x] **7.3 Question bookmarks** — Let users bookmark questions to review later
- [x] **7.4 AI assistant** — Explain why an answer is correct/incorrect (wire the existing placeholder button)
- [ ] **7.5 Learning centers map** — Show nearby driving schools on a map (wire the existing placeholder button)

---

## Status Log

| Date | What was done |
|------|---------------|
| 2026-04-21 | Initial roadmap created |
| 2026-04-21 | Phase 1 complete — DB persistence wired, real stats on HomeScreen, image fallback |
| 2026-04-21 | Phase 2 mostly done — StatsScreen (accuracy, categories, history), ReviewMistakesScreen; difficulty distribution left for later |
| 2026-04-28 | Phase 3 (partial) — Category picker, weak areas practice, exam simulation with 20-min countdown timer |
| 2026-04-28 | Phase 4 complete — Sealed class navigation, back stack, bottom nav bar, slide/fade transitions, exit dialog |
| 2026-04-28 | Phase 5 (mostly done) — All strings extracted to Armenian resources; 5.4 (font/RTL check) needs on-device verification |
| 2026-04-28 | Phase 6 (5/6 done) — Dark mode, loading states, empty states, accessibility, long text; 6.6 tablet/landscape still to verify |
| 2026-04-28 | Phase 3.4 done — Configurable test length (10/20/30 chip selector on HomeScreen) |
| 2026-04-29 | Phase 7.2 done — Daily streak: UserStreak table, streak chip on HomeScreen |
| 2026-04-29 | Phase 7.3 done — Bookmarks: toggle on QuestionDetailScreen, BookmarksScreen, practice from bookmarks |
| 2026-04-29 | Phase 7.4 done — AI assistant: Anthropic API via Ktor, API key from env var (BuildConfig/Info.plist), AiExplanationScreen |
