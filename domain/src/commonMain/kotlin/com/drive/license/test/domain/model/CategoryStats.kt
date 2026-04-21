package com.drive.license.test.domain.model

data class CategoryStats(
    val categoryName: String,
    val totalQuestions: Int,
    val totalAttempts: Int,
    val totalCorrect: Int
) {
    val accuracy: Float
        get() = if (totalAttempts > 0) totalCorrect.toFloat() / totalAttempts else 0f

    val attempted: Boolean
        get() = totalAttempts > 0
}
