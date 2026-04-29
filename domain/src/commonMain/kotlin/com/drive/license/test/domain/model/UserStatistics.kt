package com.drive.license.test.domain.model

data class UserStatistics(
    val totalQuestions: Int = 0,
    val totalAttempts: Int = 0,
    val totalCorrect: Int = 0,
    val totalIncorrect: Int = 0,
    val learnedQuestions: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
) {
    val overallAccuracy: Float
        get() = if (totalAttempts > 0) totalCorrect.toFloat() / totalAttempts else 0f
}
