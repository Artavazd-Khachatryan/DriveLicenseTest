package com.drive.license.test.domain.model

/**
 * Domain model for a question - represents business logic entities
 * This is independent of data layer implementation
 */
data class Question(
    val id: Int,
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
    val imageUrl: String? = null,
    val category: String,
    val difficulty: String
) 