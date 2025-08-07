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
    val book: Book,
    val categories: List<QuestionCategory> = emptyList()
)

enum class Book {
    BOOK_1,
    BOOK_2,
    BOOK_3,
    BOOK_4,
    BOOK_5,
    BOOK_6,
    BOOK_7,
    BOOK_8,
    BOOK_9,
    BOOK_10
}

enum class QuestionCategory {
    TRAFFIC_SIGNS_AND_MARKINGS,
    LANE_USAGE_AND_POSITIONING,
    MANEUVERS_AND_TURNS,
    RIGHT_OF_WAY_AND_PRIORITY,
    PROHIBITED_ACTIONS,
    SPECIAL_VEHICLES_AND_SITUATIONS,
    INTERSECTIONS_AND_CROSSINGS,
    ROAD_CONDITIONS_AND_VISIBILITY,
    VEHICLE_TYPES_AND_CATEGORIES,
    GENERAL_TRAFFIC_RULES
} 