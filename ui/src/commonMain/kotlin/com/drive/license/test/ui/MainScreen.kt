package com.drive.license.test.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.QuestionCategory
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.domain.repository.UserProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold

@Composable
fun MainScreen(
    questionRepository: QuestionRepository,
    userProgressRepository: UserProgressRepository,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    var route by remember { mutableStateOf("home") }
    var testSession by remember { mutableStateOf<TestSession?>(null) }
    var userStatistics by remember { mutableStateOf(UserStatistics()) }
    var examRemainingSeconds by remember { mutableStateOf<Int?>(null) }
    val allQuestions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        userStatistics = userProgressRepository.getUserStatistics()
    }

    // Exam countdown — ticks while route == "question" and session is exam mode
    LaunchedEffect(testSession?.isExamMode, testSession?.sessionId) {
        val session = testSession ?: return@LaunchedEffect
        if (!session.isExamMode) {
            examRemainingSeconds = null
            return@LaunchedEffect
        }
        examRemainingSeconds = session.examDurationSeconds
        while ((examRemainingSeconds ?: 0) > 0 && route == "question") {
            delay(1000)
            examRemainingSeconds = (examRemainingSeconds ?: 0) - 1
        }
        // Time's up — auto-complete
        if ((examRemainingSeconds ?: 1) == 0 && route == "question") {
            val current = testSession
            if (current != null) {
                val completed = current.copy(isCompleted = true)
                testSession = completed
                coroutineScope.launch {
                    val now = Clock.System.now().toEpochMilliseconds()
                    userProgressRepository.saveTestSession(
                        sessionId = current.sessionId,
                        startTime = current.startTime,
                        endTime = now,
                        totalQuestions = current.questions.size,
                        correctAnswers = completed.correctAnswers
                    )
                    userStatistics = userProgressRepository.getUserStatistics()
                }
                route = "results"
            }
        }
    }

    when (route) {
        "home" -> HomeScreen(
            userStatistics = userStatistics,
            onStartTest = {
                val randomQuestions = allQuestions.shuffled().take(20)
                testSession = TestSession(questions = randomQuestions)
                route = "question"
            },
            onOpenStats = { route = "stats" },
            onOpenFailed = { route = "mistakes" },
            onOpenPractice = { route = "practice" },
            onOpenChat = { },
            onOpenMap = { },
            onOpenStatsFromRing = { route = "stats" },
            modifier = modifier
        )
        "stats" -> StatsScreen(
            userProgressRepository = userProgressRepository,
            onBack = { route = "home" }
        )
        "mistakes" -> ReviewMistakesScreen(
            userProgressRepository = userProgressRepository,
            onBack = { route = "home" }
        )
        "practice" -> {
            var weakAreaCount by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                weakAreaCount = questionRepository.getWeakAreaQuestions().size
            }
            PracticeModeScreen(
                weakAreaCount = weakAreaCount,
                onPickCategory = { route = "category_picker" },
                onStartWeakAreas = {
                    coroutineScope.launch {
                        val weak = questionRepository.getWeakAreaQuestions()
                        if (weak.isNotEmpty()) {
                            testSession = TestSession(questions = weak.take(20))
                            route = "question"
                        }
                    }
                },
                onStartExam = {
                    val examQuestions = allQuestions.shuffled().take(TestSession.EXAM_QUESTION_COUNT)
                    testSession = TestSession(questions = examQuestions, isExamMode = true)
                    route = "question"
                },
                onBack = { route = "home" }
            )
        }
        "category_picker" -> CategoryPickerScreen(
            categories = remember(allQuestions) { allQuestions.toCategoryInfoList() },
            onSelectCategory = { category ->
                coroutineScope.launch {
                    val questions = questionRepository.getQuestionsByCategory(category)
                        .first()
                        .shuffled()
                        .take(20)
                    if (questions.isNotEmpty()) {
                        testSession = TestSession(questions = questions)
                        route = "question"
                    }
                }
            },
            onBack = { route = "practice" }
        )
        "question" -> testSession?.let { session ->
            QuestionDetailScreen(
                question = session.currentQuestion,
                questionNumber = session.currentQuestionIndex + 1,
                totalQuestions = session.questions.size,
                selectedAnswer = session.answers[session.currentQuestionIndex],
                remainingSeconds = if (session.isExamMode) examRemainingSeconds else null,
                onBack = { route = "home" },
                onAnswer = { answer ->
                    val updatedSession = session.answerQuestion(answer)
                    testSession = updatedSession
                    coroutineScope.launch {
                        userProgressRepository.saveQuestionAttempt(
                            sessionId = session.sessionId,
                            questionId = session.currentQuestion.id,
                            selectedAnswer = answer,
                            isCorrect = answer == session.currentQuestion.correctAnswer,
                            attemptTime = Clock.System.now().toEpochMilliseconds()
                        )
                    }
                },
                onNext = {
                    val next = session.nextQuestion()
                    testSession = next
                    if (next.isCompleted) {
                        coroutineScope.launch {
                            val now = Clock.System.now().toEpochMilliseconds()
                            userProgressRepository.saveTestSession(
                                sessionId = session.sessionId,
                                startTime = session.startTime,
                                endTime = now,
                                totalQuestions = session.questions.size,
                                correctAnswers = next.correctAnswers
                            )
                            userStatistics = userProgressRepository.getUserStatistics()
                        }
                        route = "results"
                    }
                },
                onPrevious = {
                    testSession = session.previousQuestion()
                }
            )
        }
        "results" -> TestResultsScreen(
            session = testSession!!,
            onBackToHome = {
                route = "home"
                testSession = null
                examRemainingSeconds = null
            },
            onRetakeTest = {
                val randomQuestions = allQuestions.shuffled().take(20)
                testSession = TestSession(questions = randomQuestions)
                route = "question"
            }
        )
    }
}
