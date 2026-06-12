package com.drive.license.test.domain.model

data class UserStatistics(
    val totalQuestions: Int = 0,
    val totalAttempts: Int = 0,
    val totalCorrect: Int = 0,
    val totalIncorrect: Int = 0,
    val learnedQuestions: Int = 0,
    val questionsSeen: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
) {
    val overallAccuracy: Float
        get() = if (totalAttempts > 0) totalCorrect.toFloat() / totalAttempts else 0f

    /** Share of the full question bank marked learned (net-correct margin > 3). */
    val learningProgress: Float
        get() = if (totalQuestions > 0) learnedQuestions.toFloat() / totalQuestions else 0f

    /** Share of the question bank the user has attempted at least once. */
    val coverageProgress: Float
        get() = if (totalQuestions > 0) questionsSeen.toFloat() / totalQuestions else 0f

    val questionsRemaining: Int
        get() = (totalQuestions - learnedQuestions).coerceAtLeast(0)

    val questionsUnseen: Int
        get() = (totalQuestions - questionsSeen).coerceAtLeast(0)
}
