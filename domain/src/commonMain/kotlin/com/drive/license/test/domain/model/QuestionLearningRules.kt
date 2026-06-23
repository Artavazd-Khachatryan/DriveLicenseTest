package com.drive.license.test.domain.model

/**
 * Shared rules for per-question learning progress and the mistakes review list.
 *
 * - A question leaves **Review mistakes** after [CORRECT_ANSWERS_TO_CLEAR_MISTAKE] correct answers
 *   (only while it has at least one incorrect attempt on record).
 * - A question is **learned** when (timesCorrect - timesIncorrect) > [NET_CORRECT_MARGIN_FOR_LEARNED].
 */
object QuestionLearningRules {
    const val CORRECT_ANSWERS_TO_CLEAR_MISTAKE = 3
    const val NET_CORRECT_MARGIN_FOR_LEARNED = 2

    fun isLearned(timesCorrect: Int, timesIncorrect: Int, isLearnedFlag: Boolean = false): Boolean =
        isLearnedFlag || (timesCorrect - timesIncorrect) > NET_CORRECT_MARGIN_FOR_LEARNED

    fun isInMistakes(timesCorrect: Int, timesIncorrect: Int): Boolean =
        timesIncorrect > 0 && timesCorrect < CORRECT_ANSWERS_TO_CLEAR_MISTAKE

    fun remainingCorrectToClearMistake(timesCorrect: Int, timesIncorrect: Int): Int? {
        if (!isInMistakes(timesCorrect, timesIncorrect)) return null
        return (CORRECT_ANSWERS_TO_CLEAR_MISTAKE - timesCorrect).coerceAtLeast(0)
    }

    /** Net correct answers still needed to reach the learned threshold. */
    fun remainingNetCorrectToLearn(timesCorrect: Int, timesIncorrect: Int, isLearnedFlag: Boolean = false): Int? {
        if (isLearned(timesCorrect, timesIncorrect, isLearnedFlag)) return null
        val targetNet = NET_CORRECT_MARGIN_FOR_LEARNED + 1
        return (targetNet - (timesCorrect - timesIncorrect)).coerceAtLeast(0)
    }
}
