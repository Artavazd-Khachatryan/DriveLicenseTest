package com.drive.license.test.domain.model

data class QuestionProgress(
    val questionId: Int,
    val timesAnswered: Int = 0,
    val timesCorrect: Int = 0,
    val timesIncorrect: Int = 0,
    val isLearned: Boolean = false,
) {
    val isLearnedByScore: Boolean
        get() = QuestionLearningRules.isLearned(timesCorrect, timesIncorrect, isLearned)

    val isInMistakes: Boolean
        get() = QuestionLearningRules.isInMistakes(timesCorrect, timesIncorrect)

    val remainingToLearn: Int?
        get() = QuestionLearningRules.remainingNetCorrectToLearn(timesCorrect, timesIncorrect, isLearned)

    val remainingToClearMistake: Int?
        get() = QuestionLearningRules.remainingCorrectToClearMistake(timesCorrect, timesIncorrect)
}
