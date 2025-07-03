package com.drive.license.test.database

import com.drive.license.test.repository.QuestionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Utility for exporting the database after it has been populated.
 * This can be called from the app to create a pre-populated database file.
 */
class DatabaseExportUtil(
    private val questionRepository: QuestionRepository,
    private val coroutineScope: CoroutineScope
) {
    
    /**
     * Exports the database after ensuring it's populated.
     * Call this after the database has been initialized with all questions.
     */
    fun exportDatabaseIfPopulated() {
        coroutineScope.launch {
            val isPopulated = isDatabasePopulated()
            if (isPopulated) {
                println("[DEBUG_LOG] Database is populated, ready for export")
                println("[DEBUG_LOG] To export the database:")
                println("[DEBUG_LOG] 1. Use DatabaseLoader.exportDatabase()")
                println("[DEBUG_LOG] 2. Copy the exported file to src/commonMain/resources/prepopulated_database.db")
                println("[DEBUG_LOG] 3. The database will be included in future builds")
            } else {
                println("[DEBUG_LOG] Database is not populated yet")
            }
        }
    }
    
    /**
     * Checks if the database is properly populated.
     */
    private suspend fun isDatabasePopulated(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val questions = questionRepository.getAllQuestions().first()
                questions.isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }
    }
} 