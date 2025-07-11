package com.drive.license.test.models

data class UserStatistics(
    val totalQuestions: Int = 0,
    val totalAttempts: Int = 0,
    val totalCorrect: Int = 0,
    val totalIncorrect: Int = 0,
    val learnedQuestions: Int = 0
) {
    val overallAccuracy: Float
        get() = if (totalAttempts > 0) totalCorrect.toFloat() / totalAttempts else 0f
    
    val progressPercentage: Float
        get() = if (totalQuestions > 0) (learnedQuestions.toFloat() / totalQuestions) * 100 else 0f
    
    val questionsRemaining: Int
        get() = totalQuestions - learnedQuestions
}



data class TestScore(
    val correctAnswers: Int,
    val totalQuestions: Int,
    val totalAnswered: Int,
    val timeSpent: Long? = null
) {
    val percentage: Float
        get() = if (totalAnswered > 0) (correctAnswers.toFloat() / totalAnswered) * 100 else 0f
    
    val isPassed: Boolean
        get() = percentage >= 70f
} 