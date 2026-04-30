package com.drive.license.test.database.repository

import com.drive.license.test.database.Database
import com.drive.license.test.domain.model.BookmarkedQuestion
import com.drive.license.test.domain.model.CategoryStats
import com.drive.license.test.domain.model.MistakeQuestion
import com.drive.license.test.domain.model.TestSessionSummary
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.domain.repository.UserProgressRepository as DomainUserProgressRepository

class UserProgressRepository(private val database: Database) : DomainUserProgressRepository {

    override suspend fun getUserStatistics(): UserStatistics {
        val stats = database.getUserStatistics()
        val streak = database.getStreak()
        return UserStatistics(
            totalQuestions = stats.totalQuestions,
            totalAttempts = stats.totalAttempts,
            totalCorrect = stats.totalCorrect,
            totalIncorrect = stats.totalIncorrect,
            learnedQuestions = stats.learnedQuestions,
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak
        )
    }

    override suspend fun getCategoryStats(): List<CategoryStats> {
        return database.getCategoryAccuracy().map { row ->
            CategoryStats(
                categoryName = row.category_name,
                totalQuestions = row.total_questions.toInt(),
                totalAttempts = row.total_attempts.toInt(),
                totalCorrect = row.total_correct.toInt()
            )
        }
    }

    override suspend fun getTestHistory(): List<TestSessionSummary> {
        return database.getCompletedTestSessions().map { row ->
            TestSessionSummary(
                id = row.id,
                startTime = row.start_time,
                endTime = row.end_time,
                totalQuestions = row.total_questions.toInt(),
                correctAnswers = row.correct_answers.toInt()
            )
        }
    }

    override suspend fun getMistakeQuestions(): List<MistakeQuestion> {
        return database.getIncorrectQuestions().map { row ->
            MistakeQuestion(
                id = row.id.toInt(),
                question = row.question,
                correctAnswer = row.true_answer,
                userAnswer = row.last_wrong_answer
            )
        }
    }

    override suspend fun saveTestSession(
        sessionId: String,
        startTime: Long,
        endTime: Long,
        totalQuestions: Int,
        correctAnswers: Int
    ) {
        database.insertTestSession(sessionId, startTime, totalQuestions)
        database.completeTestSession(sessionId, endTime, correctAnswers)
    }

    override suspend fun saveQuestionAttempt(
        sessionId: String,
        questionId: Int,
        selectedAnswer: String,
        isCorrect: Boolean,
        attemptTime: Long
    ) {
        database.insertQuestionAttempt(
            sessionId = sessionId,
            questionId = questionId.toLong(),
            selectedAnswer = selectedAnswer,
            isCorrect = isCorrect,
            timeSpent = null,
            attemptTime = attemptTime
        )
        database.updateQuestionProgress(
            questionId = questionId.toLong(),
            isCorrect = isCorrect,
            timestamp = attemptTime
        )
    }

    override suspend fun updateStreak(todayEpochDay: Long) {
        database.updateStreak(todayEpochDay)
    }

    override suspend fun getBookmarkedQuestions(): List<BookmarkedQuestion> {
        return database.getBookmarkedQuestions().map { row ->
            BookmarkedQuestion(
                id = row.id.toInt(),
                question = row.question,
                correctAnswer = row.true_answer
            )
        }
    }

    override suspend fun isBookmarked(questionId: Int): Boolean {
        return database.isBookmarked(questionId.toLong())
    }

    override suspend fun toggleBookmark(questionId: Int, bookmarkedAt: Long) {
        if (database.isBookmarked(questionId.toLong())) {
            database.removeBookmark(questionId.toLong())
        } else {
            database.addBookmark(questionId.toLong(), bookmarkedAt)
        }
    }
}
