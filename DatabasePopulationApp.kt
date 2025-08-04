package com.drive.license.test

import com.drive.license.test.database.Database
import com.drive.license.test.database.DatabaseDriverFactory
import com.drive.license.test.models.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

/**
 * Standalone application to populate the database from QuestionGroup classes.
 * Run this once to create the properly populated database file.
 */

fun main() {
    println("=== Database Population Started ===")
    
    // Create database connection
    val databaseDriverFactory = DatabaseDriverFactory()
    val database = Database(databaseDriverFactory)
    
    // Clear existing data first (run the SQL script)
    println("Step 1: Clearing existing data...")
    // Note: Run populate_database_script.sql first manually
    
    // Populate questions from all groups
    println("Step 2: Populating questions from QuestionGroup classes...")
    populateAllQuestions(database)
    
    println("=== Database Population Completed ===")
}

fun populateAllQuestions(database: Database) {
    val allQuestionGroups = listOf(
        "QuestionGroup1" to QuestionGroup1.questions,
        "QuestionGroup2" to QuestionGroup2.questions,
        "QuestionGroup3" to QuestionGroup3.questions,
        "QuestionGroup4" to QuestionGroup4.questions,
        "QuestionGroup5" to QuestionGroup5.questions,
        "QuestionGroup6" to QuestionGroup6.questions,
        "QuestionGroup7" to QuestionGroup7.questions,
        "QuestionGroup8" to QuestionGroup8.questions,
        "QuestionGroup9" to QuestionGroup9.questions,
        "QuestionGroup10" to QuestionGroup10.questions
    )
    
    var totalCount = 0
    
    allQuestionGroups.forEach { (groupName, questions) ->
        println("Processing $groupName with ${questions.size} questions...")
        
        questions.forEachIndexed { index, question ->
            try {
                insertQuestionToDatabase(database, question, totalCount + index + 1)
                totalCount++
                
                if (totalCount % 100 == 0) {
                    println("  Processed $totalCount questions...")
                }
            } catch (e: Exception) {
                println("  Error inserting question ${totalCount + 1}: ${e.message}")
            }
        }
    }
    
    println("Successfully inserted $totalCount questions")
    printFinalSummary(database)
}

fun insertQuestionToDatabase(database: Database, question: Question, questionId: Long) {
    // Create a DatabaseQuestion from the Question
    val databaseQuestion = DatabaseQuestion(
        id = questionId,
        question = getStringFromResource(question.question),
        image = question.image,
        answers = question.answers.map { getStringFromResource(it) },
        trueAnswer = getStringFromResource(question.trueAnswer),
        book = question.book,
        categories = question.categories
    )
    
    // Insert using the database's insert method
    database.insertDatabaseQuestion(databaseQuestion)
}

/**
 * Helper function to extract string from StringResource.
 * This is a simplified version - in practice you might need to handle this differently.
 */
fun getStringFromResource(resource: org.jetbrains.compose.resources.StringResource): String {
    // This is a placeholder - you'll need to implement proper string resource handling
    // For now, we'll use the resource key/name as the string
    return resource.toString().substringAfterLast(".").replace("_", " ")
}

fun printFinalSummary(database: Database) {
    println("\n=== Final Database Summary ===")
    
    // Get total counts using flows (simplified)
    try {
        // This is simplified - you might need to collect the flows properly
        println("Database populated successfully!")
        println("Next steps:")
        println("1. Check the database file at: composeApp/src/commonMain/resources/license_test_questions.db")
        println("2. Export this database")
        println("3. Replace the old database file")
        println("4. Clean up the population code")
    } catch (e: Exception) {
        println("Error getting summary: ${e.message}")
    }
}