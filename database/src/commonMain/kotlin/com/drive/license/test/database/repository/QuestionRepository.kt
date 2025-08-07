package com.drive.license.test.database.repository

import com.drive.license.test.database.Database
import com.drive.license.test.database.models.Book
import com.drive.license.test.database.models.DatabaseQuestion
import com.drive.license.test.database.models.QuestionCategory
import com.drive.license.test.domain.repository.QuestionRepository as DomainQuestionRepository
import com.drive.license.test.domain.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

class QuestionRepository(private val database: Database) : DomainQuestionRepository {
    
    // Domain interface implementation
    override fun getAllQuestions(): Flow<List<Question>> {
        return database.getAllQuestions().map { databaseQuestions ->
            databaseQuestions.map { it.toDomainModel() }
        }
    }
    
    fun getQuestionById(id: Long): DatabaseQuestion? {
        return database.getQuestionById(id)
    }
    
    fun getQuestionsByBook(book: Book): Flow<List<DatabaseQuestion>> {
        return database.getQuestionsByBook(book)
    }
    
    fun getQuestionsByCategory(category: QuestionCategory): Flow<List<DatabaseQuestion>> {
        return database.getQuestionsByCategory(category)
    }
    
    // Domain interface implementation
    override fun getQuestionsByCategory(category: String): Flow<List<Question>> {
        return database.getAllQuestions().map { questions ->
            questions.filter { question ->
                question.categories.any { it.name == category }
            }.map { it.toDomainModel() }
        }
    }
    
    override fun getQuestionsByDifficulty(difficulty: String): Flow<List<Question>> {
        // For now, return all questions since difficulty is not in the database model
        return database.getAllQuestions().map { questions ->
            questions.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getRandomQuestions(count: Int): List<Question> {
        return database.getAllQuestions().map { questions ->
            questions.shuffled().take(count).map { it.toDomainModel() }
        }.first()
    }
    
    override suspend fun getQuestionById(id: Int): Question? {
        return database.getQuestionById(id.toLong())?.toDomainModel()
    }
    
    fun insertDatabaseQuestion(databaseQuestion: DatabaseQuestion): Long {
        return database.insertDatabaseQuestion(databaseQuestion)
    }
    
    fun deleteQuestion(id: Long) {
        database.deleteQuestion(id)
    }
    
    fun deleteAllQuestions() {
        database.deleteAllQuestions()
    }
    
    private fun DatabaseQuestion.toDomainModel(): Question {
        return Question(
            id = this.id.toInt(),
            question = this.question,
            answers = this.answers,
            correctAnswer = this.trueAnswer,
            imageUrl = this.image,
            category = this.categories.firstOrNull()?.name ?: "GENERAL",
            difficulty = "MEDIUM" // Default difficulty since it's not in the database model
        )
    }
}