package com.drive.license.test.database

import com.drive.license.test.database.DatabaseDriverFactory
import com.drive.license.test.repository.QuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for database functionality.
 */
class DatabaseTest {
    
    @Test
    fun testDatabaseInitialization() = runBlocking {
        println("[TEST] Starting database initialization test...")
        
        // Create database driver factory (this will be mocked in tests)
        val driverFactory = createTestDatabaseDriverFactory()
        
        // Create database
        val database = Database(driverFactory)
        val questionRepository = QuestionRepository(database)
        
        // Test database access
        val isAccessible = testDatabaseAccess(questionRepository)
        assertTrue(isAccessible, "Database should be accessible")
        
        println("[TEST] Database initialization test passed!")
    }
    
    @Test
    fun testDatabaseQueries() = runBlocking {
        println("[TEST] Starting database queries test...")
        
        val driverFactory = createTestDatabaseDriverFactory()
        val database = Database(driverFactory)
        val questionRepository = QuestionRepository(database)
        
        // Test getting all questions
        val questions = withContext(Dispatchers.IO) {
            questionRepository.getAllQuestions().first()
        }
        
        // For now, we expect the database to be empty since we removed the migration logic
        assertEquals(0, questions.size, "Database should be empty initially")
        
        println("[TEST] Database queries test passed!")
    }
    
    @Test
    fun testDatabaseExportUtil() = runBlocking {
        println("[TEST] Starting database export util test...")
        
        val driverFactory = createTestDatabaseDriverFactory()
        val database = Database(driverFactory)
        val questionRepository = QuestionRepository(database)
        
        // Test export utility
        val exportUtil = DatabaseExportUtil(questionRepository, kotlinx.coroutines.CoroutineScope(Dispatchers.IO))
        
        // This should not crash even with empty database
        exportUtil.exportDatabaseIfPopulated()
        
        println("[TEST] Database export util test passed!")
    }
    
    /**
     * Tests if the database is accessible and can be queried.
     */
    private suspend fun testDatabaseAccess(questionRepository: QuestionRepository): Boolean {
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
     * Creates a test database driver factory.
     * In a real test environment, this would be mocked.
     */
    private fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
        // For testing purposes, we'll create a simple implementation
        return object : DatabaseDriverFactory() {
            override fun createDriver() = createTestDriver()
        }
    }
    
    /**
     * Creates a test database driver.
     * This is a simplified version for testing.
     */
    private fun createTestDriver(): Any {
        // This would be a mock or in-memory database driver
        return object {
            // Mock implementation
        }
    }
} 