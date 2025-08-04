package com.drive.license.test.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.drive.license.test.LicenseDatabase
import com.drive.license.test.models.Book
import com.drive.license.test.models.DatabaseQuestion
import com.drive.license.test.models.QuestionCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()
    private val database = LicenseDatabase(driver = driver)
    
    private val questionQueries = database.questionQueries
    private val bookQueries = database.bookQueries
    private val categoryQueries = database.questionCategoryQueries
    private val junctionQueries = database.questionCategoryJunctionQueries
    
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
        // Clear junction table first to avoid foreign key constraints
        junctionQueries.deleteAllQuestionCategories()
        questionQueries.deleteAllQuestions()
    }
}