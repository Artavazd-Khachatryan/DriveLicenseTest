package com.drive.license.test.database.repository

import com.drive.license.test.database.Database
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.domain.repository.UserProgressRepository as DomainUserProgressRepository

class UserProgressRepository(private val database: Database) : DomainUserProgressRepository {

    override suspend fun getUserStatistics(): UserStatistics {
        val stats = database.getUserStatistics()
        return UserStatistics(
            totalQuestions = stats.totalQuestions,
            totalAttempts = stats.totalAttempts,
            totalCorrect = stats.totalCorrect,
            totalIncorrect = stats.totalIncorrect,
            learnedQuestions = stats.learnedQuestions
        )
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
}
