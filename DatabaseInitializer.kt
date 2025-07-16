package com.drive.license.test.database

import com.drive.license.test.models.Book
import com.drive.license.test.models.QuestionCategory
import com.drive.license.test.models.DatabaseQuestion
import com.drive.license.test.repository.QuestionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Database initializer that populates the database with questions from QuestionGroup objects.
 * This class can be integrated into your app to automatically populate the database.
 */
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
                println("[DEBUG_LOG] Failed to load pre-populated database, populating from QuestionGroups...")
                populateFromQuestionGroups()
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
    
    private suspend fun populateFromQuestionGroups() {
        withContext(Dispatchers.IO) {
            try {
                println("[DEBUG_LOG] Starting database population from QuestionGroups...")
                
                // Import all QuestionGroup objects
                val allQuestions = mutableListOf<DatabaseQuestion>()
                
                // QuestionGroup1
                allQuestions.addAll(QuestionGroup1.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // QuestionGroup2
                allQuestions.addAll(QuestionGroup2.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // QuestionGroup3
                allQuestions.addAll(QuestionGroup3.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // QuestionGroup4
                allQuestions.addAll(QuestionGroup4.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // QuestionGroup5
                allQuestions.addAll(QuestionGroup5.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // QuestionGroup6
                allQuestions.addAll(QuestionGroup6.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // QuestionGroup7
                allQuestions.addAll(QuestionGroup7.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // QuestionGroup8
                allQuestions.addAll(QuestionGroup8.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // QuestionGroup9
                allQuestions.addAll(QuestionGroup9.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // QuestionGroup10
                allQuestions.addAll(QuestionGroup10.questions.map { question ->
                    DatabaseQuestion(
                        id = question.hashCode().toLong(),
                        question = question.question,
                        image = question.image,
                        answers = question.answers,
                        trueAnswer = question.trueAnswer,
                        book = question.book,
                        categories = question.categories
                    )
                })
                
                // Insert all questions into database
                allQuestions.forEach { databaseQuestion ->
                    questionRepository.insertDatabaseQuestion(databaseQuestion)
                }
                
                println("[DEBUG_LOG] Successfully populated database with ${allQuestions.size} questions from QuestionGroups")
                
            } catch (e: Exception) {
                println("[DEBUG_LOG] Error populating database from QuestionGroups: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}