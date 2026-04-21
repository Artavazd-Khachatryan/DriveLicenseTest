package com.drive.license.test.domain.model

data class TestSessionSummary(
    val id: String,
    val startTime: Long,
    val endTime: Long?,
    val totalQuestions: Int,
    val correctAnswers: Int
) {
    val score: Float
        get() = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f

    val passed: Boolean
        get() = score >= 0.7f
}
