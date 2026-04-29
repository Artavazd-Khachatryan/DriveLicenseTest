package com.drive.license.test.domain.repository

interface AiAssistant {
    suspend fun explainAnswer(
        questionText: String,
        userAnswer: String,
        correctAnswer: String,
        isCorrect: Boolean
    ): String
}
