package com.drive.license.test.database

import com.drive.license.test.repository.QuestionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Test class for verifying database functionality.
 * This can be used to test the pre-populated database system.
 */
class DatabaseTest(
    private val questionRepository: QuestionRepository,
    private val coroutineScope: CoroutineScope
) {
    
    /**
     * Tests the database loading and querying functionality.
     */
    fun runDatabaseTest() {
        coroutineScope.launch {
            println("[TEST] Starting database test...")
            
            // Test 1: Check if database is accessible
            val isAccessible = testDatabaseAccess()
            println("[TEST] Database accessible: $isAccessible")
            
            // Test 2: Check question count
            val questionCount = getQuestionCount()
            println("[TEST] Question count: $questionCount")
            
            // Test 3: Test specific queries
            testSpecificQueries()
            
            println("[TEST] Database test completed!")
        }
    }
    
    /**
     * Tests if the database is accessible and can be queried.
     */
    private suspend fun testDatabaseAccess(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val questions = questionRepository.getAllQuestions().first()
                true
            } catch (e: Exception) {
                println("[TEST] Database access error: ${e.message}")
                false
            }
        }
    }
    
    /**
     * Gets the total number of questions in the database.
     */
    private suspend fun getQuestionCount(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val questions = questionRepository.getAllQuestions().first()
                questions.size
            } catch (e: Exception) {
                println("[TEST] Error getting question count: ${e.message}")
                0
            }
        }
    }
    
    /**
     * Tests specific database queries.
     */
    private suspend fun testSpecificQueries() {
        withContext(Dispatchers.IO) {
            try {
                // Test getting questions by book
                val questions = questionRepository.getAllQuestions().first()
                if (questions.isNotEmpty()) {
                    val firstQuestion = questions.first()
                    println("[TEST] First question: ${firstQuestion.question.take(50)}...")
                    println("[TEST] First question book: ${firstQuestion.book}")
                    println("[TEST] First question categories: ${firstQuestion.categories}")
                }
            } catch (e: Exception) {
                println("[TEST] Error in specific queries: ${e.message}")
            }
        }
    }
    
    /**
     * Tests the export functionality (for development).
     */
    fun testExportFunctionality() {
        coroutineScope.launch {
            println("[TEST] Testing export functionality...")
            
            val exportUtil = DatabaseExportUtil(questionRepository, coroutineScope)
            exportUtil.exportDatabaseIfPopulated()
            
            println("[TEST] Export test completed!")
        }
    }
} 