package com.drive.license.test.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuestionLearningRulesTest {

    @Test
    fun mistakeClearsAfterThreeCorrectAnswers() {
        assertTrue(QuestionLearningRules.isInMistakes(timesCorrect = 0, timesIncorrect = 1))
        assertTrue(QuestionLearningRules.isInMistakes(timesCorrect = 2, timesIncorrect = 1))
        assertFalse(QuestionLearningRules.isInMistakes(timesCorrect = 3, timesIncorrect = 1))
    }

    @Test
    fun learnedRequiresNetMarginGreaterThanOne() {
        assertFalse(QuestionLearningRules.isLearned(timesCorrect = 1, timesIncorrect = 0))
        assertFalse(QuestionLearningRules.isLearned(timesCorrect = 2, timesIncorrect = 1))
        assertTrue(QuestionLearningRules.isLearned(timesCorrect = 2, timesIncorrect = 0))
        assertTrue(QuestionLearningRules.isLearned(timesCorrect = 3, timesIncorrect = 1))
        assertFalse(QuestionLearningRules.isLearned(timesCorrect = 3, timesIncorrect = 2))
    }

    @Test
    fun remainingCounts() {
        assertEquals(3, QuestionLearningRules.remainingCorrectToClearMistake(timesCorrect = 0, timesIncorrect = 2))
        assertEquals(1, QuestionLearningRules.remainingCorrectToClearMistake(timesCorrect = 2, timesIncorrect = 2))
        assertEquals(2, QuestionLearningRules.remainingNetCorrectToLearn(timesCorrect = 0, timesIncorrect = 0))
        assertEquals(1, QuestionLearningRules.remainingNetCorrectToLearn(timesCorrect = 1, timesIncorrect = 0))
        assertEquals(1, QuestionLearningRules.remainingNetCorrectToLearn(timesCorrect = 2, timesIncorrect = 1))
    }
}
