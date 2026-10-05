# Release QA checklist

Manual and on-device verification, one section per user-facing feature. Run it through `/pre-release-qa` (see `.claude/skills/pre-release-qa/SKILL.md`), which also runs `scripts/release_check.sh` first.

## How to maintain

- Every feature PR must add or update its section here and, where feasible, a Compose smoke test in `androidApp/src/androidTest/kotlin/com/drive/license/test/SmokeTest.kt`.
- The `pre-release-qa` skill fails the check if a screen file under `ui/src/commonMain/kotlin/com/drive/license/test/ui/` changed since the last release tag and no section here covers it.
- Armenian labels must be copied from `ui/src/commonMain/composeResources/values/strings.xml` (key in brackets), never typed from memory.
- Keep the "Automated by" line honest: name the test, or write "manual only".
- Fixed numbers used below (from code): practice lengths 10/20/30 (`HomeScreen`); practice pass mark 70% (`results_failed_subtitle`); theory exam 20 questions, 30 minutes (1800 s), at most 2 mistakes, i.e. 18 correct to pass (`TestSession.EXAM_*`); colour-vision exam 3 random plates, all must be correct (`ColorVisionTestRules.EXAM_QUESTION_COUNT`); expanded layout from 600dp width, content max width 720dp (`AppLayout`); bank sizes ABC 1050 (843 general + 207 ABC), DT 1380 (843 general + 537 DT) from `exam_group` in the bundled DB.

Conventions: "Home" = bottom tab `Գլխավոր`, "Practice" = `Պարապմունք`, "Progress" = `Առաջընթաց`. Default test device: 1080x2340 @420dpi. Take a screenshot at the expected result of every section.

---

## 1. First-launch category chooser

- Entry: fresh install (or after clearing app data, only for this section); dialog shown over Home.
- Labels: title `Ո՞ր կարգին եք պատրաստվում` [exam_paper_title], options `A, B, C` / `D, T`, hint `Կարող եք փոխել Կարգավորումներում։`, button `Շարունակել`.
- Steps:
  1. Fresh install and launch.
  2. Confirm the dialog appears, with a default option preselected.
  3. Press system back and tap outside the dialog: it must not close.
  4. Select `D, T`, tap `Շարունակել`.
  5. Confirm Home shows with a question total of 1380 in the progress text (`%1$d / %2$d հարց սովորած`).
  6. Relaunch the app: the dialog must not appear again.
- Expected: dialog is modal and one-time; choice persists across restarts.
- Automated by: `SmokeTest.ensureHome` (picks `A, B, C` and continues, setup only); the rest is manual only.

## 2. Home

- Entry: bottom tab `Գլխավոր`.
- Labels: `Պատրաստ ե՞ք հանձնել քննությունը`, `Ձեր առաջընթացը`, `Սկսել պարապել`, `Սկսել`, `Արագ գործողություններ`, `Վերանայել սխալները`, `Ավտոդպրոցներ`, rings `Ճշգրտություն` / `Սովորած` / `Դիտված`.
- Steps:
  1. Launch with existing progress and confirm the progress rings and the `Շարք` (streak) chip render.
  2. Tap an accuracy/learned/seen ring: Progress screen opens.
  3. Return to Home, tap the `Կարգավորումներ` icon: Settings opens; go back.
  4. Scroll Home: feature cards for traffic signs, colour vision, crossing order and driving schools are present (`AI Օգնական` must NOT show; `AppFeatures.aiEnabled = false`).
  5. Tap `Բացել ցանկը` on driving schools: list opens with filter `Բոլորը`, city sections, `Հասցե` / `Հեռախոս` labels; go back.
- Expected: no crash on any card; motivation text matches state (first visit text when no attempts).
- Automated by: `SmokeTest.appLaunchesToHome`, `SmokeTest.bottomNavOpensPracticeAndProgress` (nav only); rest manual only.

## 3. Home practice session (10/20/30) to results

- Entry: Home, `Սկսել պարապել` card, length chips `10 հարց` / `20 հարց` / `30 հարց` [home_question_count], button `Սկսել`.
- Steps:
  1. Select `10 հարց`, tap `Սկսել`.
  2. Top bar reads `Հարց 1 / 10-ից` [question_number_of_total]; no countdown timer is shown.
  3. Tap an answer; `Հաջորդ` becomes enabled; `Նախորդ` goes back and keeps the earlier answer.
  4. Repeat the same with `20 հարց` and `30 հարց` (at least start and check the counter total, then exit).
  5. Answer all 10; the last button reads `Ավարտել`.
  6. Results screen `Թեստի արդյունքներ`: score, `Ճիշտ պատասխաններ`, `Սխալ պատասխաններ`, `Պատասխանված հարցեր`.
  7. Tap `Սկսել նորից` for a new session, then back with `Վերադառնալ գլխավոր`.
- Expected: result passes at >= 70% (`Թեստը հաղթահարված է`), otherwise `Շարունակեք պատրաստվել!` with `Անցնելու համար անհրաժեշտ է 70%`. Home statistics update.
- Exit dialog: during a session tap Back: `Ելնե՞լ թեստից` with `Ելք` / `Շարունակել`; `Շարունակել` keeps the session.
- Automated by: `SmokeTest.fullPracticeSession` (10 questions to results); 20/30 and exit dialog manual only.

## 4. Question screen: bookmark, theme toggle, context sheet

- Entry: any question screen.
- Labels: `Ավելացնել էջանշան` / `Հեռացնել էջանշաններից` (bookmark icon), `Վերցնել համատեքստը` (context sheet, title `Հարցի համատեքստ`, buttons `Պատճենել`).
- Steps:
  1. Open a session; tap the bookmark icon: description changes to `Հեռացնել էջանշաններից`.
  2. Go to the next question and back: bookmark state is kept.
  3. Toggle the theme from the question top bar: colours switch without losing the session.
  4. Where the context button `Վերցնել համատեքստը` is shown, open it, tap `Պատճենել`, confirm `Պատճենված է։ Բացիր չատը և տեղադրիր։`.
  5. Question images (when present) render; if not, `Նկարն անհասանելի է` is shown and counts as a defect.
- Expected: no state loss, images load for questions that have one.
- Automated by: manual only. (Context-sheet visibility conditions could not be determined from MainScreen alone; check `QuestionContextSheet.kt`.)

## 5. Review wrong answers

- Entry: Practice tab, first card `Վերանայել սխալները` [home_review_mistakes_title], button `Դիտել`; also from Results.
- Steps:
  1. Finish a session with at least 2 wrong answers.
  2. On Results, open the mistakes review (or Practice, `Վերանայել սխալները`).
  3. Screen shows `Վերանայելու` count and, per question, `Ձեր պատասխանը` and `Ճիշտ պատասխան`.
  4. Tap `Պարապել`: a session starts with exactly the mistake questions.
  5. Answer a mistake correctly (repeatedly if needed): it leaves the list once answered correctly enough.
  6. With no mistakes (fresh data): `Սխալներ դեռ չկան!` and the Practice card shows `Դեռ սխալներ չկան`.
- Expected: counts on Practice card and review screen agree.
- Automated by: manual only.

## 6. Bookmarks

- Entry: Practice tab, 4th card `Էջանշված հարցեր`, button `Պարապել`.
- Steps:
  1. With no bookmarks: card shows `Նախ ավելացրեք հարցերը էջանշաններում:` and the button is disabled.
  2. Bookmark 2-3 questions during a session (section 4).
  3. Return to Practice: card shows `Պարապեք պահված հարցերով։` and the button is enabled.
  4. Open it: `Էջանշաններ` list with `%d հարց պահված`, each with `Ճիշտ պատասխան`.
  5. Tap `Պարապել էջանշվածներով`: a session with exactly the bookmarked questions starts.
  6. Remove all bookmarks; the Practice card is disabled again; the list shows `Էջանշված հարցեր դեռ չկան`.
- Expected: enabled state strictly follows bookmark count.
- Automated by: manual only.

## 7. Progress tab and test history

- Entry: bottom tab `Առաջընթաց`.
- Labels: `Կատարողականություն`, `Քանակ`, `Ճիշտ`, `Սխալ`, `Ըստ կատեգորիայի`, `Թեստերի պատմություն`, per row `Հաջողված` / `Ձախողված`, hint `Սեղմեք՝ պատասխանները դիտելու համար`.
- Steps:
  1. With no data: `Ավարտված թեստեր դեռ չկան։` and `Տվյալներ չկան — ...` for categories.
  2. After sessions: totals match what was answered; category bars show.
  3. History on Progress shows only the latest 5 finished sessions, with `%d/%d ճիշտ` and pass/fail badge. With 6 or more, `Ընդլայնել` opens the full list under the same title.
  4. Tap a row on either list: `Թեստի վերանայում` with `Հարց N`, `Ձեր պատասխանը`, `Ճիշտ պատասխանը`.
  5. Tap a question: single question review opens; Back returns step by step.
- Expected: history is paper-scoped (switching category in Settings shows that paper's data only).
- Automated by: `SmokeTest.bottomNavOpensPracticeAndProgress` (opens tab, checks `Կատարողականություն`); rest manual only.

## 8. Practice by category

- Entry: Practice tab, card `Ըստ կատեգորիայի`, button `Ընտրել`.
- Steps:
  1. Open the picker `Ընտրել կատեգորիա`; categories list (e.g. `Ճանապարհային նշաններ և գծանշումներ`, `Մանևրներ և շրջադարձեր`) with `%d հարց` counts.
  2. Tap one category: a 20-question session of that category starts (counter `/ 20-ից`, or fewer if the category is smaller).
  3. Finish or exit; Back from the picker returns to Practice.
- Expected: all questions come from the chosen category; counts match the bank for the selected paper.
- Automated by: manual only.

## 9. Exam simulation (colour-vision plates, then timed theory exam)

- Entry: Practice tab, card `Քննության սիմուլյացիա` ("3 գունային + 20 տեսական հարց · 30 րոպե · առավելագույնը 2 սխալ։"), button `Սկսել`.
- Numbers: 3 colour plates, all must be correct; then 20 theory questions; 30 minutes; pass with at most 2 mistakes (18/20).
- Steps:
  1. Tap `Սկսել`: plate screen `Թիթեղ 1 / 3`, prompt `Ո՞ր թիվն է պատկերված նկարում` or `Ի՞նչ պատկեր եք տեսնում նկարում`; the plate image renders (not `Նկարը հասանելի չէ`).
  2. Answer all 3 plates correctly: results `Լավ արդյունք` with `Շարունակել տեսական քննությունը`.
  3. Tap it: theory exam starts, `Հարց 1 / 20-ից`, countdown (`Մնացած ժամանակը`) starts near 30:00 and decreases.
  4. Fail path: restart, answer one plate wrong: `Արդյունքը բավարար չէ`, exam stops (`Քննության սիմուլյացիան դադարեցված է։`), no theory questions; `Սկսել նորից` draws new plates.
  5. In the theory exam answer 18 right and 2 wrong: Results shows passed. Repeat with 3 wrong: failed (needs a second run; may be shortened by answering quickly).
  6. Timer expiry: let it run out; results open automatically and unanswered questions count as wrong. If 30 minutes is not affordable, mark SKIPPED, but verify the timer ticks for 10+ seconds and keeps running across `Հաջորդ`/`Նախորդ`.
  7. `Սկսել նորից` on the exam results starts with the colour plates again.
- Expected: flow plates -> theory -> results; threshold exactly 2 mistakes.
- Automated by: manual only (host tests cover `TestSession`/`ColorVisionSession` logic in the `ui`/`domain` modules, names not verified).

## 10. Colour-vision practice (standalone)

- Entry: Home card `Գունային տեսողություն` (button `Սկսել`) or Practice card `Գունային տեսողություն` (`Բացել թեստը`); intro screen `Հոգեբանական թեստ`.
- Steps:
  1. Intro shows rules (`Քննության ժամանակ հարցվում է 3 պատահական թիթեղ ...`), bank count `Թեստային բազա՝ N թիթեղ` and the disclaimer.
  2. Tap `Քննության սիմուլյացիա (3 հարց)`: 3 plates, then `Արդյունքներ`.
  3. Back, tap `Բոլոր թիթեղները (N)`: all plates in order, `Թիթեղ k / N`.
  4. Results offer `Սկսել նորից` and Back to Home.
- Expected: every plate image loads; answers can be changed with `Նախորդ`/`Հաջորդ`.
- Automated by: manual only.

## 11. Traffic sign catalog with filter

- Entry: Home feature card `Ճանապարհային նշաններ`, button `Դիտել`.
- Steps:
  1. Catalog opens with items and filter chips: `Բոլորը`, `Նախազգուշացնող`, `Առավելության`, `Արգելող`, `Թելադրող`, `Հատուկ թելադրանքի`, `Տեղեկատվության`, `Սպասարկման`, `Ցուցանակներ`.
  2. Select each chip: list shrinks to that category and is non-empty; `Բոլորը` restores everything.
  3. Tap a sign: detail opens with image and meaning; `Փակել` closes it.
  4. Scroll fast through the list: images load, no blank cards.
- Expected: sign images render; no crash on rapid filter switching.
- Automated by: `SmokeTest.trafficSignCatalogShowsItems` (opens catalog, waits for `sign_card`); filter and detail manual only.

## 12. Crossing order (`Խաչմերուկի հերթ`)

- Entry: Home feature card `Խաչմերուկի հերթ`, button `Սկսել`; top bar button `Սովորել` opens the guide `Ինչպես լուծել`.
- Steps:
  1. Open it: scenario title and the prompt `Հպեք մեքենաներին 1, 2, 3… հերթով, ապա սեղմեք «Ստուգել»։`; `Ստուգել` is disabled; counter `1 / N`.
  2. Tap vehicles in order: numbers 1, 2, 3 appear on them; `Ձեր հերթը՝ ...` fills; tapping a picked vehicle again removes it.
  3. `Ստուգել` becomes enabled only when all vehicles are picked.
  4. Correct order: tap `Ստուգել`; vehicles drive through one by one (`Ընթացքի մեջ է…`), result `Ճիշտ է` and the line `Հերթը ճիշտ է։ <explanation>`.
  5. `Մաքրել`, then a wrong order: `Ստուգել`; the early vehicle drives out and a crash is shown, `Սխալ է`, text `... բախվում է նրան։`, then `Ճիշտ հերթը՝ ...` and `Հերթը տարբեր է։ <explanation>`.
  6. After a wrong answer, tap `Ստուգել` again to replay the animation of the order (the "show crossing" replay).
  7. `Նախորդը` / `Հաջորդը` move between scenarios; picks reset per scenario.
  8. Open `Սովորել`: guide sections render; `Անցնել հարցերին` returns.
- Expected: explanation text matches the rule of the scenario (right-hand rule, main road, lights, special signals); no overlap of cars with the road drawing in light or dark.
- Verified 2026-10-05 on the emulator: the screen has `Հետ`, `Սովորել`, `Նախորդը`/`Հաջորդը`, `Մաքրել` and `Ստուգել` only. There is no `Ցույց տալ անցումը` button (the string is imported but unused); `Ստուգել` triggers the animation.
- Automated by: manual only (rules logic may be covered by host tests under `ui/src/commonTest`; names not verified).

## 13. Settings

- Entry: Home, `Կարգավորումներ` icon.
- Labels: `Ընտրեք կարգը` (`A, B, C` / `D, T`), `Տեսք` (`Բաց` / `Մուգ`), `Օրական պարապմունքի հիշեցում`, `Զրոյացնել վիճակագրությունը`, `Տարբերակ x.y.z.n`.
- Steps:
  1. Category: select `D, T`: Home total becomes 1380; select `A, B, C`: 1050. Progress and mistakes are scoped per paper.
  2. Theme: switch `Մուգ` then `Բաց`; the whole app restyles at once; relaunch keeps the choice.
  3. Reminder: enable `Օրական պարապմունքի հիշեցում`; grant the notification permission (Android 13+); `Հիշեցման ժամ` appears; `Փոխել ժամը` opens `Ընտրեք հիշեցման ժամը`; set a time. Deny the permission once: `Ծանուցումները թույլատրված չեն։ ...` shows and the switch does not stay on.
  4. Version label matches `scripts/print_app_version.sh` output for the build.
  5. Reset: `Զրոյացնել` opens `Զրոյացնե՞լ վիճակագրությունը`; `Չեղարկել` keeps data; confirm clears Progress, history, mistakes, streak; bookmarks and the chosen paper stay (verify; if bookmarks vanish, report as should-fix).
- Expected: totals 1050 / 1380 exactly.
- Automated by: `SmokeTest.settingsSwitchCategoryAndBack` (category switch both ways, back to Home; no totals check); rest manual only.

## 14. Dark mode on every main screen

- Entry: Settings `Տեսք` `Մուգ`.
- Steps: in dark mode visit and screenshot each of: first-launch chooser (fresh install only), Home, Practice, Progress (with history), question screen (answered state with correct and wrong colours), Results, mistakes review, bookmarks, category picker, colour-vision plate and results, traffic signs catalog and detail, crossing order (idle, correct, wrong), driving schools, Settings, dialogs (exit, reset).
- Expected: no white or black hard-coded surfaces, text contrast readable, answer correct/wrong states distinguishable by icon or label and not only colour, colour-vision plate images unaffected.
- Automated by: manual only.

## 15. Upgrade from the previous release keeps progress

- Entry: not a screen; data migration (`ContentRefresh`, `content_version`).
- Automated by: host test `database/src/androidHostTest/kotlin/com/drive/license/test/database/ContentUpgradeTest.kt` (upgrade from the v1.1.0.14 release DB fixture with progress and bookmarks), run by `scripts/release_check.sh`.
- Manual emulator variant (run whenever the bundled DB or `ContentRefresh` changed):
  1. `git worktree add /tmp/prev <previous tag>` and build its debug APK (`./gradlew :androidApp:assembleDebug`), or use a stored previous APK.
  2. Install it on a clean emulator, pick a paper, finish 2 sessions with some wrong answers, bookmark 2 questions, set the theme to dark.
  3. Note: Home progress numbers, Progress history count, mistakes count, bookmark count.
  4. Install the new build over it with `adb install -r` (never uninstall, never clear data).
  5. Launch: no chooser, same paper, theme kept; all counts from step 3 are unchanged; practice session and image questions still work; new content (changed questions) visible if the release changed content.
- Expected: progress, bookmarks, history, settings survive; no crash or `SQLiteException` in logcat.

## 16. Layout: phone and expanded, light and dark

- Sizes: phone 1080x2340 @420dpi (portrait) and an expanded window (tablet, foldable open, or landscape; >= 600dp wide, content centred and capped at 720dp).
- Steps: for each size and each theme, open Home, Practice, Progress, a question screen with an image, Results, traffic signs, crossing order, Settings.
  - Phone: no horizontal scroll, no clipped text, 16dp gutters, touch targets reachable, bottom bar not covering buttons or the last list item, gesture bar inset respected.
  - Expanded: content width at most 720dp and centred, bottom bar usable, question image not stretched, crossing stage stays square and does not overflow.
  - Rotate during a session and during crossing animation: state is kept.
- Expected: all screens usable at both sizes in both themes.
- Automated by: manual only.
