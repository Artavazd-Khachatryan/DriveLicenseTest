package com.drive.license.test.domain.model

data class WeakAreaQuestion(
    val id: Int,
    val question: String,
    val correctAnswer: String,
    val timesIncorrect: Int,
    val timesCorrect: Int,
)
