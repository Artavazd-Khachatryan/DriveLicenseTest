package com.drive.license.test.database.repository

import com.drive.license.test.database.Database
import com.drive.license.test.database.models.Book
import com.drive.license.test.database.models.DatabaseQuestion
import com.drive.license.test.database.models.QuestionCategory
import kotlinx.coroutines.flow.Flow

class QuestionRepository(private val database: Database) {
    
    fun getAllQuestions(): Flow<List<DatabaseQuestion>> {
        return database.getAllQuestions()
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
    
    fun insertDatabaseQuestion(databaseQuestion: DatabaseQuestion): Long {
        return database.insertDatabaseQuestion(databaseQuestion)
    }
    
    fun deleteQuestion(id: Long) {
        database.deleteQuestion(id)
    }
    
    fun deleteAllQuestions() {
        database.deleteAllQuestions()
    }
}