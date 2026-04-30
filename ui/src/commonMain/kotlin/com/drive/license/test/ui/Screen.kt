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
    object Map : Screen()
    data class AiExplanation(
        val questionText: String,
        val userAnswer: String,
        val correctAnswer: String,
        val isCorrect: Boolean
    ) : Screen()

    val isTopLevel: Boolean
        get() = this is Home || this is Stats || this is Practice
}
