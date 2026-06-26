package com.drive.license.test.database.models

data class WeakAreaQuestionRow(
    val id: Int,
    val question: String,
    val correctAnswer: String,
    val timesIncorrect: Int,
    val timesCorrect: Int,
)
