package com.drive.license.test.ui

sealed class Screen {
    object Home : Screen()
    object Stats : Screen()
    object Practice : Screen()
    object CategoryPicker : Screen()
    object Question : Screen()
    object Results : Screen()
    object Mistakes : Screen()
    object Bookmarks : Screen()
    object DrivingSchools : Screen()
    object ColorVisionIntro : Screen()
    object ColorVisionTest : Screen()
    object ColorVisionResults : Screen()
    object Settings : Screen()
    data class AiExplanation(
        val questionText: String,
        val userAnswer: String,
        val correctAnswer: String,
        val isCorrect: Boolean
    ) : Screen()

    data class TestSessionReview(val sessionId: String) : Screen()

    data class TestSessionQuestionReview(
        val questionId: Int,
        val selectedAnswer: String,
        val questionNumber: Int,
    ) : Screen()

    val isTopLevel: Boolean
        get() = this is Home || this is Stats || this is Practice
}
