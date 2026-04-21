package com.drive.license.test.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.drive.license.test.database.models.Book
import com.drive.license.test.database.models.DatabaseQuestion
import com.drive.license.test.database.models.QuestionCategory
import com.drive.license.test.database.models.UserStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class Database(databaseDriverFactory: DatabaseDriverFactory) {

    companion object {
        const val POPULATED_DB_NAME = "license_test_questions.db"
    }

    private val driver = databaseDriverFactory.createDriver()
    private val database = LicenseDatabase(driver = driver)
    
    private val questionQueries = database.questionQueries
    private val bookQueries = database.bookQueries
    private val categoryQueries = database.questionCategoryQueries
    private val junctionQueries = database.questionCategoryJunctionQueries
    private val userProgressQueries = database.userProgressQueries
    
    fun getAllQuestions(): Flow<List<DatabaseQuestion>> {
        return questionQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbQuestions ->
                dbQuestions.map { dbQuestion ->
                    mapDbQuestionToDatabaseQuestion(dbQuestion)
                }
            }
    }
    
    fun getQuestionById(id: Long): DatabaseQuestion? {
        val dbQuestion = questionQueries.selectById(id).executeAsOneOrNull() ?: return null
        return mapDbQuestionToDatabaseQuestionById(dbQuestion)
    }
    
    fun getQuestionsByBook(book: Book): Flow<List<DatabaseQuestion>> {
        val bookId = bookQueries.selectByName(book.name).executeAsOne().id
        return questionQueries.selectByBook(bookId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbQuestions ->
                dbQuestions.map { dbQuestion ->
                    mapDbQuestionToDatabaseQuestionByBook(dbQuestion)
                }
            }
    }
    
    fun getQuestionsByCategory(category: QuestionCategory): Flow<List<DatabaseQuestion>> {
        val categoryId = categoryQueries.selectByName(category.name).executeAsOne().id
        return junctionQueries.selectQuestionsForCategory(categoryId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbQuestions ->
                dbQuestions.map { dbQuestion ->
                    mapDbQuestionToDatabaseQuestionByCategory(dbQuestion)
                }
            }
    }
    
    private fun mapDbQuestionToDatabaseQuestion(dbQuestion: SelectAll): DatabaseQuestion {
        val bookEnum = Book.valueOf(dbQuestion.book_name)
        val categories = junctionQueries.selectCategoriesForQuestion(dbQuestion.id)
            .executeAsList()
            .map { QuestionCategory.valueOf(it.name) }
        
        val answersList = parseAnswersFromJson(dbQuestion.answers)
        
        return DatabaseQuestion(
            id = dbQuestion.id,
            question = dbQuestion.question,
            image = dbQuestion.image,
            answers = answersList,
            trueAnswer = dbQuestion.true_answer,
            book = bookEnum,
            categories = categories
        )
    }
    
    private fun mapDbQuestionToDatabaseQuestionById(dbQuestion: SelectById): DatabaseQuestion {
        val bookEnum = Book.valueOf(dbQuestion.book_name)
        val categories = junctionQueries.selectCategoriesForQuestion(dbQuestion.id)
            .executeAsList()
            .map { QuestionCategory.valueOf(it.name) }
        
        // Parse JSON answers string back to list
        val answersList = parseAnswersFromJson(dbQuestion.answers)
        
        return DatabaseQuestion(
            id = dbQuestion.id,
            question = dbQuestion.question,
            image = dbQuestion.image,
            answers = answersList,
            trueAnswer = dbQuestion.true_answer,
            book = bookEnum,
            categories = categories
        )
    }
    
    private fun mapDbQuestionToDatabaseQuestionByBook(dbQuestion: SelectByBook): DatabaseQuestion {
        val bookEnum = Book.valueOf(dbQuestion.book_name)
        val categories = junctionQueries.selectCategoriesForQuestion(dbQuestion.id)
            .executeAsList()
            .map { QuestionCategory.valueOf(it.name) }

        val answersList = parseAnswersFromJson(dbQuestion.answers)
        
        return DatabaseQuestion(
            id = dbQuestion.id,
            question = dbQuestion.question,
            image = dbQuestion.image,
            answers = answersList,
            trueAnswer = dbQuestion.true_answer,
            book = bookEnum,
            categories = categories
        )
    }
    
    private fun mapDbQuestionToDatabaseQuestionByCategory(dbQuestion: SelectQuestionsForCategory): DatabaseQuestion {
        val bookEnum = Book.valueOf(dbQuestion.book_name)
        val categories = junctionQueries.selectCategoriesForQuestion(dbQuestion.id)
            .executeAsList()
            .map { QuestionCategory.valueOf(it.name) }

        val answersList = parseAnswersFromJson(dbQuestion.answers)
        
        return DatabaseQuestion(
            id = dbQuestion.id,
            question = dbQuestion.question,
            image = dbQuestion.image,
            answers = answersList,
            trueAnswer = dbQuestion.true_answer,
            book = bookEnum,
            categories = categories
        )
    }
    
    private fun parseAnswersFromJson(answersJson: String): List<String> {
        return try {
            Json.decodeFromString(ListSerializer(String.serializer()), answersJson)
        } catch (_: Exception) {
            answersJson.split(",").map { it.trim() }
        }
    }
    
    private fun answersToJson(answers: List<String>): String {
        return Json.encodeToString(ListSerializer(String.serializer()), answers)
    }
    
    fun insertDatabaseQuestion(databaseQuestion: DatabaseQuestion): Long {
        val bookId = bookQueries.selectByName(databaseQuestion.book.name).executeAsOne().id
        
        val answersJson = answersToJson(databaseQuestion.answers)
        
        questionQueries.insertQuestion(
            question = databaseQuestion.question,
            image = databaseQuestion.image,
            answers = answersJson,
            true_answer = databaseQuestion.trueAnswer,
            book_id = bookId
        )
        
        val questionId = questionQueries.selectAll().executeAsList().last().id
        
        databaseQuestion.categories.forEach { category ->
            val categoryId = categoryQueries.selectByName(category.name).executeAsOne().id
            junctionQueries.insertQuestionCategory(questionId, categoryId)
        }
        
        return questionId
    }
    
    fun deleteQuestion(id: Long) {
        questionQueries.deleteQuestion(id)
    }
    
    fun deleteAllQuestions() {
        junctionQueries.deleteAllQuestionCategories()
        questionQueries.deleteAllQuestions()
    }

    // --- User Progress Operations ---

    suspend fun getUserStatistics(): UserStatistics = withContext(Dispatchers.IO) {
        val row = userProgressQueries.getUserStatistics().executeAsOne()
        UserStatistics(
            totalQuestions = row.total_questions.toInt(),
            totalAttempts = row.total_attempts.toInt(),
            totalCorrect = row.total_correct.toInt(),
            totalIncorrect = row.total_incorrect.toInt(),
            learnedQuestions = row.learned_questions.toInt()
        )
    }

    suspend fun insertTestSession(id: String, startTime: Long, totalQuestions: Int) = withContext(Dispatchers.IO) {
        userProgressQueries.insertTestSession(id, startTime, totalQuestions.toLong())
    }

    suspend fun completeTestSession(id: String, endTime: Long, correctAnswers: Int) = withContext(Dispatchers.IO) {
        userProgressQueries.updateTestSessionCompletion(endTime, correctAnswers.toLong(), id)
    }

    suspend fun insertQuestionAttempt(
        sessionId: String,
        questionId: Long,
        selectedAnswer: String,
        isCorrect: Boolean,
        timeSpent: Long?,
        attemptTime: Long
    ) = withContext(Dispatchers.IO) {
        userProgressQueries.insertQuestionAttempt(
            session_id = sessionId,
            question_id = questionId,
            selected_answer = selectedAnswer,
            is_correct = if (isCorrect) 1L else 0L,
            time_spent = timeSpent,
            attempt_time = attemptTime
        )
    }

    suspend fun updateQuestionProgress(
        questionId: Long,
        isCorrect: Boolean,
        timestamp: Long
    ) = withContext(Dispatchers.IO) {
        val existing = userProgressQueries.getQuestionProgress(questionId).executeAsOneOrNull()
        if (existing == null) {
            userProgressQueries.insertQuestionProgress(
                question_id = questionId,
                times_correct = if (isCorrect) 1L else 0L,
                times_incorrect = if (isCorrect) 0L else 1L,
                last_answered_at = timestamp
            )
        } else {
            userProgressQueries.updateQuestionProgress(
                times_correct = if (isCorrect) 1L else 0L,
                times_incorrect = if (isCorrect) 0L else 1L,
                last_answered_at = timestamp,
                question_id = questionId
            )
        }
    }
}