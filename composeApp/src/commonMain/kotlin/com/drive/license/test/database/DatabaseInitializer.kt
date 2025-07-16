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
            val success = checkDatabaseStatus()

            if (success) {
                val questionCount = questionRepository.getAllQuestions().first().size
                println("[DEBUG_LOG] Database status checked - contains $questionCount questions")
            } else {
                println("[DEBUG_LOG] Failed to check database status")
            }
        }
    }

    private suspend fun checkDatabaseStatus(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val questions = questionRepository.getAllQuestions().first()
                if (questions.isEmpty()) {
                    println("[DEBUG_LOG] Database is empty. Will be populated from Compose UI.")
                } else {
                    println("[DEBUG_LOG] Database already contains ${questions.size} questions")
                }
                true
            } catch (e: Exception) {
                println("[DEBUG_LOG] Error checking database: ${e.message}")
                false
            }
        }
    }
}
