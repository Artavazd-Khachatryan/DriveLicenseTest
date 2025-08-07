package com.drive.license.test.domain.repository

import com.drive.license.test.domain.model.Question
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository interface - defines the contract for question data access
 * This is the domain layer's way of defining what data operations are available
 */
interface QuestionRepository {
    fun getAllQuestions(): Flow<List<Question>>
    fun getQuestionsByCategory(category: String): Flow<List<Question>>
    fun getQuestionsByDifficulty(difficulty: String): Flow<List<Question>>
    suspend fun getRandomQuestions(count: Int): List<Question>
    suspend fun getQuestionById(id: Int): Question?
} 