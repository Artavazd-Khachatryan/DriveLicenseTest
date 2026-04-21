package com.drive.license.test.domain.repository

import com.drive.license.test.domain.model.CategoryStats
import com.drive.license.test.domain.model.MistakeQuestion
import com.drive.license.test.domain.model.TestSessionSummary
import com.drive.license.test.domain.model.UserStatistics

interface UserProgressRepository {
    suspend fun getUserStatistics(): UserStatistics
    suspend fun getCategoryStats(): List<CategoryStats>
    suspend fun getTestHistory(): List<TestSessionSummary>
    suspend fun getMistakeQuestions(): List<MistakeQuestion>
    suspend fun saveTestSession(sessionId: String, startTime: Long, endTime: Long, totalQuestions: Int, correctAnswers: Int)
    suspend fun saveQuestionAttempt(sessionId: String, questionId: Int, selectedAnswer: String, isCorrect: Boolean, attemptTime: Long)
}
