package com.drive.license.test

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pre-release smoke tests. Strings are copied from
 * ui/src/commonMain/composeResources/values/strings.xml (Armenian).
 */
@OptIn(ExperimentalTestApi::class)
class SmokeTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private companion object {
        const val TIMEOUT = 30_000L
        const val CHOOSER_TITLE = "Ո՞ր կարգին եք պատրաստվում"
        const val OPTION_ABC = "A, B, C"
        const val OPTION_DT = "D, T"
        const val CONTINUE = "Շարունակել"
        const val START = "Սկսել"
        const val CHIP_10 = "10 հարց"
        const val NEXT = "Հաջորդ"
        const val FINISH = "Ավարտել"
        const val RESULTS_TITLE = "Թեստի արդյունքներ"
        const val NAV_PRACTICE = "Պարապմունք"
        const val NAV_STATS = "Առաջընթաց"
        const val NAV_HOME = "Գլխավոր"
        const val PRACTICE_EXAM_TITLE = "Քննության սիմուլյացիա"
        const val STATS_OVERALL = "Կատարողականություն"
        const val SETTINGS = "Կարգավորումներ"
        const val SETTINGS_EXAM_PAPER = "Ընտրեք կարգը"
        const val BACK = "Հետ"
        const val SIGNS_TITLE = "Ճանապարհային նշաններ"
        const val SIGNS_OPEN = "Դիտել"
    }

    private fun exists(text: String) =
        rule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    /** Waits for home, answering the first-launch category chooser if it shows. */
    @Before
    fun ensureHome() {
        rule.waitUntil(TIMEOUT) { exists(CHOOSER_TITLE) || exists(START) }
        if (exists(CHOOSER_TITLE)) {
            rule.onNodeWithText(OPTION_ABC).performClick()
            rule.onNodeWithText(CONTINUE).performClick()
        }
        rule.waitUntil(TIMEOUT) { exists(START) && !exists(CHOOSER_TITLE) }
    }

    private fun clickNav(label: String) {
        rule.onAllNodesWithText(label).filterToClickable().onLast().performClick()
    }

    private fun androidx.compose.ui.test.SemanticsNodeInteractionCollection.filterToClickable() =
        filter(hasClickAction())

    private fun waitForText(text: String) {
        rule.waitUntil(TIMEOUT) { exists(text) }
    }

    @Test
    fun appLaunchesToHome() {
        rule.onAllNodesWithText(START).onFirst().fetchSemanticsNode()
    }

    @Test
    fun fullPracticeSession() {
        rule.onNodeWithText(CHIP_10).performScrollTo().performClick()
        rule.onAllNodesWithText(START).onFirst().performScrollTo().performClick()

        for (n in 1..10) {
            waitForText("Հարց $n / 10-ից")
            rule.waitUntil(TIMEOUT) {
                rule.onAllNodes(hasTestTag("answer_option_0")).fetchSemanticsNodes().isNotEmpty()
            }
            rule.onNodeWithTag("answer_option_0").performScrollTo().performClick()
            val label = if (n == 10) FINISH else NEXT
            rule.waitUntilAtLeastOneExists(hasText(label).and(isEnabled()), TIMEOUT)
            rule.onNode(hasText(label).and(isEnabled())).performClick()
        }
        waitForText(RESULTS_TITLE)
    }

    @Test
    fun bottomNavOpensPracticeAndProgress() {
        clickNav(NAV_PRACTICE)
        waitForText(PRACTICE_EXAM_TITLE)
        clickNav(NAV_STATS)
        waitForText(STATS_OVERALL)
        clickNav(NAV_HOME)
        waitForText(START)
    }

    @Test
    fun settingsSwitchCategoryAndBack() {
        rule.onNodeWithContentDescription(SETTINGS).performClick()
        waitForText(SETTINGS_EXAM_PAPER)
        rule.onNodeWithText(OPTION_DT).performClick()
        rule.waitForIdle()
        rule.onNodeWithText(OPTION_DT).fetchSemanticsNode()
        rule.onNodeWithText(OPTION_ABC).performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription(BACK).performClick()
        waitForText(START)
    }

    @Test
    fun trafficSignCatalogShowsItems() {
        rule.onNodeWithText(SIGNS_TITLE).performScrollTo()
        // The signs card is the first feature card; its action sits right after the title.
        rule.onAllNodesWithText(SIGNS_OPEN).onFirst().performScrollTo().performClick()
        rule.waitUntilAtLeastOneExists(hasTestTag("sign_card"), TIMEOUT)
    }
}
