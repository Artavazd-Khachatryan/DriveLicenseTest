package com.drive.license.test.database

import com.drive.license.test.domain.repository.QuestionRepository
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
            checkDatabaseStatus()
        }
    }

    private suspend fun checkDatabaseStatus(): Boolean {
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
