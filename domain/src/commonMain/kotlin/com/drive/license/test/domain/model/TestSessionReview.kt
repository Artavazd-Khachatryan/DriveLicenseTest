package com.drive.license.test.domain.model

data class TestSessionAnswerReview(
    val questionId: Int,
    val question: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
)

data class TestSessionReview(
    val summary: TestSessionSummary,
    val answers: List<TestSessionAnswerReview>,
)
