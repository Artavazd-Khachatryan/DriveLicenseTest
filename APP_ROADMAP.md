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

- [ ] **2.1 Build StatsScreen** — Overall accuracy, total tests taken, pass rate, streak
- [ ] **2.2 Category breakdown** — Show accuracy per category (Traffic Signs, Right of Way, etc.) using existing `QuestionCategory` enum
- [ ] **2.3 Difficulty distribution** — Show how many questions are EASY/MEDIUM/HARD/LEARNED based on `QuestionWithProgress.difficulty`
- [ ] **2.4 Test history list** — Show past test sessions (date, score, pass/fail) with ability to tap and review
- [ ] **2.5 Review Mistakes screen** — List all incorrectly answered questions; tap to see question + correct answer + user's wrong answer

---

## Phase 3: Practice Mode & Smart Selection

Move beyond random 20-question tests.

- [ ] **3.1 Practice by category** — Category picker screen, then questions filtered by selected category
- [ ] **3.2 Practice by book** — Book picker (Book 1–10), then questions from that book
- [ ] **3.3 Weak areas practice** — Auto-select questions where `needsPractice == true` from `QuestionWithProgress`
- [ ] **3.4 Configurable test length** — Let user choose 10/20/30 questions before starting
- [ ] **3.5 Exam simulation mode** — 30 questions, 20-minute timer, pass at 80% (mirrors real Armenian driving exam rules)

---

## Phase 4: Navigation & UX Polish

Replace fragile navigation and improve overall feel.

- [ ] **4.1 Type-safe navigation** — Replace string routes ("home", "stats", "question") with sealed class/enum
- [ ] **4.2 Bottom navigation bar** — Home / Practice / Stats tabs using `AppScaffold` bottomBar
- [ ] **4.3 Transitions** — Add `AnimatedContent` transitions between screens (slide, fade)
- [ ] **4.4 Back handling** — Proper back press behavior (confirm exit during active test, navigate back stack otherwise)
- [ ] **4.5 Use existing components** — Wire `QuestionCard`, `ActionCard`, `StatChip` into screens where they fit (stats, review, home)

---

## Phase 5: Armenian Localization

The app targets Armenian users preparing for the driving exam.

- [ ] **5.1 Extract all strings** — Move hardcoded English text to string resources
- [ ] **5.2 Armenian translations** — Add Armenian (`hy`) translations for all UI strings
- [ ] **5.3 Armenian as default** — Set Armenian as primary language, English as fallback
- [ ] **5.4 RTL/font check** — Verify Armenian script renders correctly on both platforms

---

## Phase 6: UI Refinements

Polish the look and feel.

- [ ] **6.1 Dark mode audit** — Test all screens in dark mode, fix any hardcoded colors
- [ ] **6.2 Long text handling** — Handle long question/answer text (scrolling, text wrapping, truncation)
- [ ] **6.3 Empty states** — Add empty state UI for: no test history, no mistakes to review, no questions in category
- [ ] **6.4 Loading states** — Add skeleton/shimmer loading while questions load from DB
- [ ] **6.5 Accessibility pass** — Add missing `contentDescription`, verify 48dp touch targets, test with TalkBack/VoiceOver
- [ ] **6.6 Tablet/landscape** — Verify 720dp max-width works, test side-by-side layout on tablets

---

## Phase 7: Stretch Goals

Nice-to-have features if time allows.

- [ ] **7.1 Achievement badges** — Badges for milestones (first test, 10 tests, 100% score, category mastery)
- [ ] **7.2 Daily streak tracker** — Track consecutive days of practice
- [ ] **7.3 Question bookmarks** — Let users bookmark questions to review later
- [ ] **7.4 AI assistant** — Explain why an answer is correct/incorrect (wire the existing placeholder button)
- [ ] **7.5 Learning centers map** — Show nearby driving schools on a map (wire the existing placeholder button)

---

## Status Log

| Date | What was done |
|------|---------------|
| 2026-04-21 | Initial roadmap created |
| 2026-04-21 | Phase 1 complete — DB persistence wired, real stats on HomeScreen, image fallback |
