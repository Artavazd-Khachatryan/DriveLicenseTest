package com.drive.license.test.database

import com.drive.license.test.repository.QuestionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseInitializer(
    private val questionRepository: QuestionRepository,
    private val coroutineScope: CoroutineScope
) {
    
    fun initializeDatabase() {
        coroutineScope.launch {
            val success = loadPrepopulatedDatabase()
            
            if (success) {
                val questionCount = questionRepository.getAllQuestions().first().size
                println("[DEBUG_LOG] Pre-populated database loaded successfully with $questionCount questions")
            } else {
                println("[DEBUG_LOG] Failed to load pre-populated database")
            }
        }
    }

    private suspend fun loadPrepopulatedDatabase(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val questions = questionRepository.getAllQuestions().first()
                questions.isNotEmpty()
            } catch (e: Exception) {
                println("[DEBUG_LOG] Error loading pre-populated database: ${e.message}")
                false
            }
        }
    }
}
