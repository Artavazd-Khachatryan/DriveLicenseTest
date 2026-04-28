package com.drive.license.test.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.domain.repository.UserProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@Composable
fun MainScreen(
    questionRepository: QuestionRepository,
    userProgressRepository: UserProgressRepository,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = backStack.last()

    var testSession by remember { mutableStateOf<TestSession?>(null) }
    var userStatistics by remember { mutableStateOf(UserStatistics()) }
    var examRemainingSeconds by remember { mutableStateOf<Int?>(null) }
    val allQuestions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())

    fun navigate(screen: Screen) {
        if (screen.isTopLevel) {
            // Tab switch — replace back stack up to the first top-level screen
            backStack.clear()
        }
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    LaunchedEffect(Unit) {
        userStatistics = userProgressRepository.getUserStatistics()
    }

    // Exam countdown
    LaunchedEffect(testSession?.isExamMode, testSession?.sessionId) {
        val session = testSession ?: return@LaunchedEffect
        if (!session.isExamMode) {
            examRemainingSeconds = null
            return@LaunchedEffect
        }
        examRemainingSeconds = session.examDurationSeconds
        while ((examRemainingSeconds ?: 0) > 0 && currentScreen is Screen.Question) {
            delay(1000)
            examRemainingSeconds = (examRemainingSeconds ?: 0) - 1
        }
        if ((examRemainingSeconds ?: 1) == 0 && currentScreen is Screen.Question) {
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
                navigate(Screen.Results)
            }
        }
    }

    when (currentScreen) {
        Screen.Home -> HomeScreen(
            userStatistics = userStatistics,
            onStartTest = {
                val randomQuestions = allQuestions.shuffled().take(20)
                testSession = TestSession(questions = randomQuestions)
                navigate(Screen.Question)
            },
            onOpenStats = { navigate(Screen.Stats) },
            onOpenFailed = { navigate(Screen.Mistakes) },
            onOpenPractice = { navigate(Screen.Practice) },
            onOpenChat = { },
            onOpenMap = { },
            onOpenStatsFromRing = { navigate(Screen.Stats) },
            modifier = modifier
        )
        Screen.Stats -> StatsScreen(
            userProgressRepository = userProgressRepository,
            onBack = { navigateBack() }
        )
        Screen.Mistakes -> ReviewMistakesScreen(
            userProgressRepository = userProgressRepository,
            onBack = { navigateBack() }
        )
        Screen.Practice -> {
            var weakAreaCount by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                weakAreaCount = questionRepository.getWeakAreaQuestions().size
            }
            PracticeModeScreen(
                weakAreaCount = weakAreaCount,
                onPickCategory = { navigate(Screen.CategoryPicker) },
                onStartWeakAreas = {
                    coroutineScope.launch {
                        val weak = questionRepository.getWeakAreaQuestions()
                        if (weak.isNotEmpty()) {
                            testSession = TestSession(questions = weak.take(20))
                            navigate(Screen.Question)
                        }
                    }
                },
                onStartExam = {
                    val examQuestions = allQuestions.shuffled().take(TestSession.EXAM_QUESTION_COUNT)
                    testSession = TestSession(questions = examQuestions, isExamMode = true)
                    navigate(Screen.Question)
                },
                onBack = { navigateBack() }
            )
        }
        Screen.CategoryPicker -> CategoryPickerScreen(
            categories = remember(allQuestions) { allQuestions.toCategoryInfoList() },
            onSelectCategory = { category ->
                coroutineScope.launch {
                    val questions = questionRepository.getQuestionsByCategory(category)
                        .first()
                        .shuffled()
                        .take(20)
                    if (questions.isNotEmpty()) {
                        testSession = TestSession(questions = questions)
                        navigate(Screen.Question)
                    }
                }
            },
            onBack = { navigateBack() }
        )
        Screen.Question -> testSession?.let { session ->
            QuestionDetailScreen(
                question = session.currentQuestion,
                questionNumber = session.currentQuestionIndex + 1,
                totalQuestions = session.questions.size,
                selectedAnswer = session.answers[session.currentQuestionIndex],
                remainingSeconds = if (session.isExamMode) examRemainingSeconds else null,
                onBack = { navigateBack() },
                onAnswer = { answer ->
                    testSession = session.answerQuestion(answer)
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
                        navigate(Screen.Results)
                    }
                },
                onPrevious = { testSession = session.previousQuestion() }
            )
        }
        Screen.Results -> TestResultsScreen(
            session = testSession!!,
            onBackToHome = {
                backStack.clear()
                backStack.add(Screen.Home)
                testSession = null
                examRemainingSeconds = null
            },
            onRetakeTest = {
                val randomQuestions = allQuestions.shuffled().take(20)
                testSession = TestSession(questions = randomQuestions)
                // replace Results with Question in back stack
                backStack.removeAt(backStack.lastIndex)
                backStack.add(Screen.Question)
            }
        )
    }
}
