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
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.domain.model.Question
import kotlinx.coroutines.CoroutineScope
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
    val allQuestions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        userStatistics = userProgressRepository.getUserStatistics()
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
            onOpenFailed = { },
            onOpenChat = { },
            onOpenMap = { },
            onOpenStatsFromRing = { route = "stats" },
            modifier = modifier
        )
        "stats" -> StatsScreen(
            userProgressRepository = userProgressRepository,
            onBack = { route = "home" }
        )
        "question" -> testSession?.let { session ->
            QuestionDetailScreen(
                question = session.currentQuestion,
                questionNumber = session.currentQuestionIndex + 1,
                totalQuestions = session.questions.size,
                selectedAnswer = session.answers[session.currentQuestionIndex],
                onBack = { route = "home" },
                onAnswer = { answer ->
                    val updatedSession = session.answerQuestion(answer)
                    testSession = updatedSession
                    // Persist each answer immediately
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
                        // Persist the completed test session
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
            },
            onRetakeTest = {
                val randomQuestions = allQuestions.shuffled().take(20)
                testSession = TestSession(questions = randomQuestions)
                route = "question"
            }
        )
    }
}

@Composable
private fun QuestionsList(
    itemsPadding: Modifier,
    questions: List<com.drive.license.test.domain.model.Question>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.then(itemsPadding).padding(16.dp)) {
        Text(
            text = "Showing ${questions.size} questions",
            style = MaterialTheme.typography.titleLarge
        )
        LazyColumn(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
        ) {
            itemsIndexed(questions) { index, q ->
                AppCard(modifier = androidx.compose.ui.Modifier.padding(vertical = 6.dp)) {
                    Column(modifier = androidx.compose.ui.Modifier.padding(12.dp)) {
                        Text(text = "${index + 1}. ${q.question}", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Answers: ${q.answers.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}