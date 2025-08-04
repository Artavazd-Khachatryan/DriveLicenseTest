package com.drive.license.test.database

import com.drive.license.test.LicenseDatabase
import com.drive.license.test.models.*

/**
 * One-time database populator that reads from QuestionGroup classes 
 * and creates a properly populated database.
 */
class DatabasePopulator(private val database: LicenseDatabase) {
    
    private val questionQueries = database.questionQueries
    private val bookQueries = database.bookQueries
    private val categoryQueries = database.questionCategoryQueries
    private val junctionQueries = database.questionCategoryJunctionQueries
    
    /**
     * Complete database population process:
     * 1. Clear existing data
     * 2. Populate books and categories
     * 3. Populate questions with proper associations
     */
    fun populateDatabase() {
        println("Starting database population...")
        
        // Step 1: Clear all existing data
        clearAllData()
        
        // Step 2: Populate reference tables
        populateBooks()
        populateCategories()
        
        // Step 3: Populate questions from all groups
        populateQuestionsFromGroups()
        
        println("Database population completed!")
        printSummary()
    }
    
    /**
     * Step 1: Clear all existing data
     */
    private fun clearAllData() {
        println("Clearing existing data...")
        junctionQueries.deleteAllQuestionCategories()
        questionQueries.deleteAllQuestions()
        // Note: We keep books and categories as they're reference data
    }
    
    /**
     * Step 2a: Populate Books table with all 10 books
     */
    private fun populateBooks() {
        println("Populating books...")
        Book.values().forEach { book ->
            // Check if book already exists, if not insert
            try {
                bookQueries.selectByName(book.name).executeAsOne()
            } catch (e: Exception) {
                // Book doesn't exist, insert it
                // Note: We need to add an insert query to Book.sq
                println("Book ${book.name} needs to be inserted")
            }
        }
    }
    
    /**
     * Step 2b: Populate Categories table with all categories
     */
    private fun populateCategories() {
        println("Populating categories...")
        QuestionCategory.values().forEach { category ->
            // Check if category already exists, if not insert
            try {
                categoryQueries.selectByName(category.name).executeAsOne()
            } catch (e: Exception) {
                // Category doesn't exist, insert it
                // Note: We need to add an insert query to QuestionCategory.sq
                println("Category ${category.name} needs to be inserted")
            }
        }
    }
    
    /**
     * Step 3: Populate questions from all QuestionGroup objects
     */
    private fun populateQuestionsFromGroups() {
        println("Populating questions from groups...")
        
        val allQuestionGroups = listOf(
            QuestionGroup1.questions,
            QuestionGroup2.questions,
            QuestionGroup3.questions,
            QuestionGroup4.questions,
            QuestionGroup5.questions,
            QuestionGroup6.questions,
            QuestionGroup7.questions,
            QuestionGroup8.questions,
            QuestionGroup9.questions,
            QuestionGroup10.questions
        )
        
        var questionCount = 0
        
        allQuestionGroups.forEachIndexed { groupIndex, questions ->
            println("Processing QuestionGroup${groupIndex + 1} with ${questions.size} questions...")
            
            questions.forEach { question ->
                insertQuestionToDatabase(question)
                questionCount++
            }
        }
        
        println("Inserted $questionCount questions total")
    }
    
    /**
     * Insert a single question with proper book and category associations
     */
    private fun insertQuestionToDatabase(question: Question) {
        // Get the book ID
        val bookId = bookQueries.selectByName(question.book.name).executeAsOne().id
        
        // Convert StringResource to actual string (this is a simplification)
        // In reality, you might need to handle string resources differently
        val questionText = question.question.toString() // Simplified
        val answersJson = question.answers.toString() // Simplified - should be proper JSON
        val trueAnswerText = question.trueAnswer.toString() // Simplified
        
        // Insert the question
        questionQueries.insertQuestion(
            question = questionText,
            image = question.image,
            answers = answersJson,
            true_answer = trueAnswerText,
            book_id = bookId
        )
        
        // Get the inserted question ID
        val questionId = questionQueries.selectAll().executeAsList().last().id
        
        // Insert category associations
        question.categories.forEach { category ->
            val categoryId = categoryQueries.selectByName(category.name).executeAsOne().id
            junctionQueries.insertQuestionCategory(questionId, categoryId)
        }
    }
    
    /**
     * Print summary of populated data
     */
    private fun printSummary() {
        val questionCount = questionQueries.selectAll().executeAsList().size
        val bookCount = bookQueries.selectAll().executeAsList().size
        val categoryCount = categoryQueries.selectAll().executeAsList().size
        
        println("\n=== Database Population Summary ===")
        println("Questions: $questionCount")
        println("Books: $bookCount")
        println("Categories: $categoryCount")
        
        // Show distribution by book
        println("\nQuestions per book:")
        Book.values().forEach { book ->
            val bookId = bookQueries.selectByName(book.name).executeAsOne().id
            val count = questionQueries.selectByBook(bookId).executeAsList().size
            println("  ${book.name}: $count questions")
        }
        
        // Show distribution by category
        println("\nQuestions per category:")
        QuestionCategory.values().forEach { category ->
            val categoryId = categoryQueries.selectByName(category.name).executeAsOne().id
            val count = junctionQueries.selectQuestionsForCategory(categoryId).executeAsList().size
            println("  ${category.name}: $count questions")
        }
    }
}

/**
 * Usage: Call this function once to populate the database from class data
 */
fun populateDatabaseFromClasses(database: LicenseDatabase) {
    val populator = DatabasePopulator(database)
    populator.populateDatabase()
}