package com.drive.license.test

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.drive.license.test.models.QuestionGroup1
import com.drive.license.test.ui.components.QuestionScreen

@Composable
fun App() {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val questionGroup = QuestionGroup1()
    val questions = questionGroup.questionsGroup1

    MaterialTheme {
        QuestionScreen(
            questions = questions,
            currentQuestionIndex = currentQuestionIndex,
            onAnswerSelected = { index ->
                // Handle answer selection
            },
            onNextQuestion = {
                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                }
            },
            onPreviousQuestion = {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--
                }
            },
            onFinish = {
                // Handle finish
            }
        )
    }
}