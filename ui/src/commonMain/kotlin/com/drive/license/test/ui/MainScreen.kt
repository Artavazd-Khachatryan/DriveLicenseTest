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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.domain.model.Question
import kotlinx.coroutines.CoroutineScope
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold

@Composable
fun MainScreen(
    questionRepository: QuestionRepository,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    var route by remember { mutableStateOf("home") }
    var testSession by remember { mutableStateOf<TestSession?>(null) }
    val allQuestions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())
    
    when (route) {
        "home" -> HomeScreen(
            onStartTest = { 
                // Create test session with 20 random questions and go directly to first question
                val randomQuestions = allQuestions.shuffled().take(20)
                testSession = TestSession(questions = randomQuestions)
                route = "question"
            },
            onOpenStats = { route = "stats" },
            onOpenFailed = { },
            onOpenChat = { },
            onOpenStatsFromRing = { route = "stats" },
            modifier = modifier
        )
        "stats" -> StatsScreen(onBack = { route = "home" })
        "question" -> testSession?.let { session ->
            QuestionDetailScreen(
                question = session.currentQuestion,
                questionNumber = session.currentQuestionIndex + 1,
                totalQuestions = session.questions.size,
                selectedAnswer = session.answers[session.currentQuestionIndex],
                onBack = { route = "home" },
                onAnswer = { answer ->
                    testSession = session.answerQuestion(answer)
                },
                onNext = {
                    testSession = session.nextQuestion()
                    if (session.nextQuestion().isCompleted) {
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
                // Create new test session and go directly to first question
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