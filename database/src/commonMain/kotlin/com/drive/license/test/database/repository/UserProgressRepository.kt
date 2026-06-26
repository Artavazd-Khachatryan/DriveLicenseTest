package com.drive.license.test.database.repository

import com.drive.license.test.database.Database
import com.drive.license.test.domain.model.BookmarkedQuestion
import com.drive.license.test.domain.model.CategoryStats
import com.drive.license.test.domain.model.MistakeQuestion
import com.drive.license.test.domain.model.QuestionProgress
import com.drive.license.test.domain.model.TestSessionAnswerReview
import com.drive.license.test.domain.model.TestSessionReview
import com.drive.license.test.domain.model.TestSessionSummary
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.domain.repository.UserProgressRepository as DomainUserProgressRepository
import com.drive.license.test.domain.util.QuestionTextNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProgressRepository(private val database: Database) : DomainUserProgressRepository {

    override suspend fun getUserStatistics(): UserStatistics {
        return withContext(Dispatchers.Default) {
            val stats = database.getUserStatistics()
            val streak = database.getStreak()
            UserStatistics(
                totalQuestions = stats.totalQuestions,
                totalAttempts = stats.totalAttempts,
                totalCorrect = stats.totalCorrect,
                totalIncorrect = stats.totalIncorrect,
                learnedQuestions = stats.learnedQuestions,
                questionsSeen = stats.questionsSeen,
                currentStreak = streak.currentStreak,
                longestStreak = streak.longestStreak
            )
        }
    }

    override suspend fun getCategoryStats(): List<CategoryStats> {
        return withContext(Dispatchers.Default) {
            database.getCategoryAccuracy().map { row ->
                CategoryStats(
                    categoryName = row.category_name,
                    totalQuestions = row.total_questions.toInt(),
                    totalAttempts = row.total_attempts.toInt(),
                    totalCorrect = row.total_correct.toInt()
                )
            }
        }
    }

    override suspend fun getTestHistory(): List<TestSessionSummary> {
        return withContext(Dispatchers.Default) {
            database.getCompletedTestSessions().map { row ->
                TestSessionSummary(
                    id = row.id,
                    startTime = row.start_time,
                    endTime = row.end_time,
                    totalQuestions = row.total_questions.toInt(),
                    correctAnswers = row.correct_answers.toInt()
                )
            }
        }
    }

    override suspend fun getTestSessionReview(sessionId: String): TestSessionReview? {
        return withContext(Dispatchers.Default) {
            val session = database.getTestSessionById(sessionId) ?: return@withContext null
            if (session.is_completed != 1L) return@withContext null
            val summary = TestSessionSummary(
                id = session.id,
                startTime = session.start_time,
                endTime = session.end_time,
                totalQuestions = session.total_questions.toInt(),
                correctAnswers = session.correct_answers.toInt(),
            )
            val answers = database.getSessionQuestionReviews(sessionId).map { row ->
                TestSessionAnswerReview(
                    questionId = row.question_id.toInt(),
                    question = QuestionTextNormalizer.normalize(row.question),
                    selectedAnswer = QuestionTextNormalizer.normalize(row.selected_answer),
                    correctAnswer = QuestionTextNormalizer.normalize(row.true_answer),
                    isCorrect = row.is_correct == 1L,
                )
            }
            TestSessionReview(summary = summary, answers = answers)
        }
    }

    override suspend fun getMistakeQuestions(): List<MistakeQuestion> {
        return withContext(Dispatchers.Default) {
            database.getIncorrectQuestions().map { row ->
                MistakeQuestion(
                    id = row.id.toInt(),
                    question = QuestionTextNormalizer.normalize(row.question),
                    correctAnswer = QuestionTextNormalizer.normalize(row.true_answer),
                    userAnswer = QuestionTextNormalizer.normalize(row.last_wrong_answer)
                )
            }
        }
    }

    override suspend fun beginTestSession(
        sessionId: String,
        startTime: Long,
        totalQuestions: Int,
    ) {
        withContext(Dispatchers.Default) {
            database.insertTestSession(sessionId, startTime, totalQuestions)
        }
    }

    override suspend fun saveTestSession(
        sessionId: String,
        startTime: Long,
        endTime: Long,
        totalQuestions: Int,
        correctAnswers: Int
    ) {
        withContext(Dispatchers.Default) {
            database.insertTestSession(sessionId, startTime, totalQuestions)
            database.completeTestSession(sessionId, endTime, correctAnswers)
        }
    }

    override suspend fun saveQuestionAttempt(
        sessionId: String,
        questionId: Int,
        selectedAnswer: String,
        isCorrect: Boolean,
        attemptTime: Long
    ) {
        withContext(Dispatchers.Default) {
            // Progress must be recorded even if attempt logging fails (e.g. missing session row).
            database.updateQuestionProgress(
                questionId = questionId.toLong(),
                isCorrect = isCorrect,
                timestamp = attemptTime
            )
            runCatching {
                database.insertQuestionAttempt(
                    sessionId = sessionId,
                    questionId = questionId.toLong(),
                    selectedAnswer = selectedAnswer,
                    isCorrect = isCorrect,
                    timeSpent = null,
                    attemptTime = attemptTime
                )
            }
        }
    }

    override suspend fun updateStreak(todayEpochDay: Long) {
        withContext(Dispatchers.Default) {
            database.updateStreak(todayEpochDay)
        }
    }

    override suspend fun getBookmarkedQuestions(): List<BookmarkedQuestion> {
        return withContext(Dispatchers.Default) {
            database.getBookmarkedQuestions().map { row ->
                BookmarkedQuestion(
                    id = row.id.toInt(),
                    question = row.question,
                    correctAnswer = row.true_answer
                )
            }
        }
    }

    override suspend fun isBookmarked(questionId: Int): Boolean {
        return withContext(Dispatchers.Default) {
            database.isBookmarked(questionId.toLong())
        }
    }

    override suspend fun toggleBookmark(questionId: Int, bookmarkedAt: Long) {
        withContext(Dispatchers.Default) {
            if (database.isBookmarked(questionId.toLong())) {
                database.removeBookmark(questionId.toLong())
            } else {
                database.addBookmark(questionId.toLong(), bookmarkedAt)
            }
        }
    }

    override suspend fun getQuestionAttemptCounts(): Map<Int, Int> {
        return withContext(Dispatchers.Default) {
            database.getQuestionAttemptCounts()
        }
    }

    override suspend fun getQuestionProgress(questionId: Int): QuestionProgress? {
        return withContext(Dispatchers.Default) {
            database.getQuestionProgress(questionId.toLong())?.let { row ->
                QuestionProgress(
                    questionId = row.question_id.toInt(),
                    timesAnswered = row.times_answered.toInt(),
                    timesCorrect = row.times_correct.toInt(),
                    timesIncorrect = row.times_incorrect.toInt(),
                    isLearned = row.is_learned == 1L,
                )
            }
        }
    }

    override suspend fun resetStatistics() {
        withContext(Dispatchers.Default) {
            database.resetStatistics()
        }
    }
}
