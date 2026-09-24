package com.drive.license.test.database.repository

import com.drive.license.test.database.Database
import com.drive.license.test.database.models.DatabaseQuestion
import com.drive.license.test.database.models.QuestionCategory as DatabaseQuestionCategory
import com.drive.license.test.database.models.Book as DatabaseBook
import com.drive.license.test.domain.repository.QuestionRepository as DomainQuestionRepository
import com.drive.license.test.domain.ExamPaperController
import com.drive.license.test.domain.model.Book
import com.drive.license.test.domain.model.Question
import com.drive.license.test.domain.model.QuestionCategory
import com.drive.license.test.domain.model.QuestionExamGroup
import com.drive.license.test.domain.model.TrafficSign
import com.drive.license.test.domain.util.QuestionTextNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class QuestionRepository(
    private val database: Database,
    private val examPaperController: ExamPaperController,
) : DomainQuestionRepository {

    override fun getTrafficSigns(): Flow<List<TrafficSign>> = database.getTrafficSigns()

    override fun getAllQuestions(): Flow<List<Question>> {
        return combine(
            database.getAllQuestions(),
            examPaperController.paper,
        ) { databaseQuestions, paper ->
            databaseQuestions
                .filter { QuestionExamGroup.fromDb(it.examGroup).isVisibleFor(paper) }
                .map { it.toDomainModel() }
        }.flowOn(Dispatchers.Default)
    }
    
    fun getQuestionById(id: Long): DatabaseQuestion? {
        return database.getQuestionById(id)
    }
    
    fun getQuestionsByBook(book: DatabaseBook): Flow<List<DatabaseQuestion>> {
        return database.getQuestionsByBook(book)
    }
    
    fun getQuestionsByCategory(category: DatabaseQuestionCategory): Flow<List<DatabaseQuestion>> {
        return database.getQuestionsByCategory(category)
    }

    override fun getQuestionsByCategory(category: QuestionCategory): Flow<List<Question>> {
        return getAllQuestions().map { questions ->
            questions.filter { question -> question.categories.any { it == category } }
        }
    }

    override fun getQuestionsByBook(book: Book): Flow<List<Question>> {
        return getAllQuestions().map { questions ->
            questions.filter { it.book == book }
        }
    }
    
    override suspend fun getRandomQuestions(count: Int): List<Question> {
        return withContext(Dispatchers.Default) {
            getAllQuestions().first().shuffled().take(count)
        }
    }
    
    override suspend fun getQuestionById(id: Int): Question? {
        return withContext(Dispatchers.Default) {
            database.getQuestionById(id.toLong())?.toDomainModel()
        }
    }

    override suspend fun getBookmarkedQuestionsForPractice(): List<Question> {
        return withContext(Dispatchers.Default) {
            database.getBookmarkedQuestionsFull(examPaperController.paper.value.name).map { it.toDomainModel() }
        }
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
            question = QuestionTextNormalizer.normalize(this.question),
            answers = QuestionTextNormalizer.normalizeAnswers(this.answers),
            correctAnswer = QuestionTextNormalizer.normalize(this.trueAnswer),
            imageUrl = this.image,
            book = this.book.toDomainModel(),
            categories = this.categories.map { it.toDomainModel() },
            examGroup = QuestionExamGroup.fromDb(this.examGroup),
        )
    }
    
    private fun DatabaseBook.toDomainModel(): Book {
        return Book.valueOf(this.name)
    }
    
    private fun DatabaseQuestionCategory.toDomainModel(): QuestionCategory {
        return QuestionCategory.valueOf(this.name)
    }
}