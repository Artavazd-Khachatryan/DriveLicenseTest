package com.drive.license.test.models

class DatabaseQuestion(
    val id: Long,
    val question: String,
    val image: String?,
    val answers: List<String>,
    val trueAnswer: String,
    val book: Book,
    val categories: List<QuestionCategory> = emptyList()
) 