package com.drive.license.test.database

import com.drive.license.test.models.*

/**
 * Helper to create a simple population script.
 * This generates SQL INSERT statements based on the QuestionGroup data.
 */

fun main() {
    println("=== Generating Population Script ===")
    
    val allQuestionGroups = listOf(
        "Group1" to QuestionGroup1.questions,
        "Group2" to QuestionGroup2.questions,
        "Group3" to QuestionGroup3.questions,
        "Group4" to QuestionGroup4.questions,
        "Group5" to QuestionGroup5.questions,
        "Group6" to QuestionGroup6.questions,
        "Group7" to QuestionGroup7.questions,
        "Group8" to QuestionGroup8.questions,
        "Group9" to QuestionGroup9.questions,
        "Group10" to QuestionGroup10.questions
    )
    
    println("-- Generated SQL script to populate questions")
    println("-- Total groups: ${allQuestionGroups.size}")
    
    var questionId = 1L
    
    allQuestionGroups.forEach { (groupName, questions) ->
        println("\n-- $groupName: ${questions.size} questions")
        
        questions.forEach { question ->
            generateInsertStatements(question, questionId)
            questionId++
        }
    }
    
    println("\n-- Population script complete. Total questions: ${questionId - 1}")
}

fun generateInsertStatements(question: Question, id: Long) {
    // Extract book ID from book enum
    val bookId = when (question.book) {
        Book.BOOK_1 -> 1
        Book.BOOK_2 -> 2
        Book.BOOK_3 -> 3
        Book.BOOK_4 -> 4
        Book.BOOK_5 -> 5
        Book.BOOK_6 -> 6
        Book.BOOK_7 -> 7
        Book.BOOK_8 -> 8
        Book.BOOK_9 -> 9
        Book.BOOK_10 -> 10
    }
    
    // For now, use placeholder text (we'll need to replace this with actual strings)
    val questionText = "Question_$id"
    val answersJson = """["Answer1", "Answer2", "Answer3"]"""
    val trueAnswer = "Answer1"
    val image = question.image ?: "NULL"
    
    println("INSERT INTO Question (id, question, image, answers, true_answer, book_id) VALUES ($id, '$questionText', '$image', '$answersJson', '$trueAnswer', $bookId);")
    
    // Insert category associations
    question.categories.forEach { category ->
        val categoryId = when (category) {
            QuestionCategory.TRAFFIC_SIGNS_AND_MARKINGS -> 1
            QuestionCategory.LANE_USAGE_AND_POSITIONING -> 2
            QuestionCategory.MANEUVERS_AND_TURNS -> 3
            QuestionCategory.RIGHT_OF_WAY_AND_PRIORITY -> 4
            QuestionCategory.PROHIBITED_ACTIONS -> 5
            QuestionCategory.SPECIAL_VEHICLES_AND_SITUATIONS -> 6
            QuestionCategory.INTERSECTIONS_AND_CROSSINGS -> 7
            QuestionCategory.ROAD_CONDITIONS_AND_VISIBILITY -> 8
            QuestionCategory.VEHICLE_TYPES_AND_CATEGORIES -> 9
            QuestionCategory.GENERAL_TRAFFIC_RULES -> 10
        }
        
        println("INSERT INTO QuestionCategoryJunction (question_id, category_id) VALUES ($id, $categoryId);")
    }
}