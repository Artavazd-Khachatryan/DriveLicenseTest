package com.drive.license.test.domain.model

data class MistakeQuestion(
    val id: Int,
    val question: String,
    val correctAnswer: String,
    val userAnswer: String
)
