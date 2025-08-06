package com.drive.license.test.database.models

data class QuestionWithProgress(
    val question: DatabaseQuestion,
    val timesAnswered: Int = 0,
    val timesCorrect: Int = 0,
    val timesIncorrect: Int = 0,
    val isLearned: Boolean = false,
    val lastAnsweredAt: Long? = null
) {
    val accuracy: Float
        get() = if (timesAnswered > 0) timesCorrect.toFloat() / timesAnswered else 0f
    
    val difficulty: QuestionDifficulty
        get() = when {
            timesCorrect >= 3 -> QuestionDifficulty.LEARNED
            timesIncorrect > timesCorrect -> QuestionDifficulty.HARD
            timesCorrect > timesIncorrect -> QuestionDifficulty.MEDIUM
            else -> QuestionDifficulty.EASY
        }
    
    val needsPractice: Boolean
        get() = !isLearned && (timesIncorrect > timesCorrect || timesAnswered == 0)
}

enum class QuestionDifficulty {
    EASY, MEDIUM, HARD, LEARNED
}

data class QuestionAttempt(
    val id: Long,
    val sessionId: String,
    val questionId: Long,
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val timeSpent: Long?,
    val attemptTime: Long
)

data class TestSession(
    val id: String,
    val startTime: Long,
    val endTime: Long? = null,
    val totalQuestions: Int,
    val correctAnswers: Int = 0,
    val isCompleted: Boolean = false
) {
    val score: Float
        get() = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f
    
    val isPassed: Boolean
        get() = score >= 0.7f
    
    val timeSpent: Long?
        get() = endTime?.let { it - startTime }
} 