package com.drive.license.test.domain.repository

import com.drive.license.test.domain.model.Question
import com.drive.license.test.domain.model.QuestionCategory
import com.drive.license.test.domain.model.Book
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository interface - defines the contract for question data access
 * This is the domain layer's way of defining what data operations are available
 */
interface QuestionRepository {
    fun getAllQuestions(): Flow<List<Question>>
    fun getQuestionsByCategory(category: QuestionCategory): Flow<List<Question>>
    fun getQuestionsByBook(book: Book): Flow<List<Question>>
    suspend fun getRandomQuestions(count: Int): List<Question>
    suspend fun getQuestionById(id: Int): Question?
} 