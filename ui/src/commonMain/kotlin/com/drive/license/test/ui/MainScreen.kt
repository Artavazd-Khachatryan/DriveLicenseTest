package com.drive.license.test.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import com.drive.license.test.domain.repository.AiAssistant
import com.drive.license.test.domain.repository.QuestionRepository
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.components.AppBottomBar
import com.drive.license.test.ui.components.BottomNavItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MainScreen(
    questionRepository: QuestionRepository,
    userProgressRepository: UserProgressRepository,
    aiAssistant: AiAssistant,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = backStack.last()

    var testSession by remember { mutableStateOf<TestSession?>(null) }
    var userStatistics by remember { mutableStateOf(UserStatistics()) }
    var examRemainingSeconds by remember { mutableStateOf<Int?>(null) }
    var currentQuestionBookmarked by remember { mutableStateOf(false) }
    val allQuestions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())

    fun navigate(screen: Screen) {
        if (screen.isTopLevel) {
            backStack.clear()
        }
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    val bottomBar: @Composable () -> Unit = {
        AppBottomBar(
            listOf(
                BottomNavItem("Home", Icons.Default.Home, currentScreen is Screen.Home) { navigate(Screen.Home) },
                BottomNavItem("Practice", Icons.Default.FitnessCenter, currentScreen is Screen.Practice) { navigate(Screen.Practice) },
                BottomNavItem("Stats", Icons.Default.BarChart, currentScreen is Screen.Stats) { navigate(Screen.Stats) }
            )
        )
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
        while ((examRemainingSeconds ?: 0) > 0 && backStack.last() is Screen.Question) {
            delay(1000)
            examRemainingSeconds = (examRemainingSeconds ?: 0) - 1
        }
        if ((examRemainingSeconds ?: 1) == 0 && backStack.last() is Screen.Question) {
            val current = testSession
            if (current != null) {
                val completed = current.copy(isCompleted = true)
                testSession = completed
                coroutineScope.launch {
                    val now = Clock.System.now()
                    userProgressRepository.saveTestSession(
                        sessionId = current.sessionId,
                        startTime = current.startTime,
                        endTime = now.toEpochMilliseconds(),
                        totalQuestions = current.questions.size,
                        correctAnswers = completed.correctAnswers
                    )
                    val todayEpochDay = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
                    userProgressRepository.updateStreak(todayEpochDay)
                    userStatistics = userProgressRepository.getUserStatistics()
                }
                navigate(Screen.Results)
            }
        }
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState.isTopLevel && initialState.isTopLevel) {
                // Tab switch: fade
                fadeIn() togetherWith fadeOut()
            } else if (!targetState.isTopLevel) {
                // Pushing onto stack: slide in from right
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it / 3 }
            } else {
                // Popping from stack: slide out to right
                slideInHorizontally { -it / 3 } togetherWith slideOutHorizontally { it }
            }
        },
        label = "screen_transition"
    ) { screen ->
    when (screen) {
        Screen.Home -> HomeScreen(
            userStatistics = userStatistics,
            onStartTest = { count ->
                val randomQuestions = allQuestions.shuffled().take(count)
                testSession = TestSession(questions = randomQuestions)
                navigate(Screen.Question)
            },
            onOpenStats = { navigate(Screen.Stats) },
            onOpenFailed = { navigate(Screen.Mistakes) },
            onOpenPractice = { navigate(Screen.Practice) },
            onOpenChat = { },
            onOpenMap = { },
            onOpenStatsFromRing = { navigate(Screen.Stats) },
            bottomBar = bottomBar,
            modifier = modifier
        )
        Screen.Stats -> StatsScreen(
            userProgressRepository = userProgressRepository,
            onBack = { navigateBack() },
            bottomBar = bottomBar
        )
        Screen.Mistakes -> ReviewMistakesScreen(
            userProgressRepository = userProgressRepository,
            onBack = { navigateBack() }
        )
        Screen.Practice -> {
            var weakAreaCount by remember { mutableStateOf(0) }
            var bookmarkCount by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                weakAreaCount = questionRepository.getWeakAreaQuestions().size
                bookmarkCount = userProgressRepository.getBookmarkedQuestions().size
            }
            PracticeModeScreen(
                weakAreaCount = weakAreaCount,
                bookmarkCount = bookmarkCount,
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
                onOpenBookmarks = { navigate(Screen.Bookmarks) },
                onBack = { navigateBack() },
                bottomBar = bottomBar
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
            LaunchedEffect(session.currentQuestion.id) {
                currentQuestionBookmarked = userProgressRepository.isBookmarked(session.currentQuestion.id)
            }
            QuestionDetailScreen(
                question = session.currentQuestion,
                questionNumber = session.currentQuestionIndex + 1,
                totalQuestions = session.questions.size,
                selectedAnswer = session.answers[session.currentQuestionIndex],
                remainingSeconds = if (session.isExamMode) examRemainingSeconds else null,
                isBookmarked = currentQuestionBookmarked,
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
                            val now = Clock.System.now()
                            userProgressRepository.saveTestSession(
                                sessionId = session.sessionId,
                                startTime = session.startTime,
                                endTime = now.toEpochMilliseconds(),
                                totalQuestions = session.questions.size,
                                correctAnswers = next.correctAnswers
                            )
                            val todayEpochDay = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
                            userProgressRepository.updateStreak(todayEpochDay)
                            userStatistics = userProgressRepository.getUserStatistics()
                        }
                        navigate(Screen.Results)
                    }
                },
                onPrevious = { testSession = session.previousQuestion() },
                onToggleBookmark = {
                    coroutineScope.launch {
                        userProgressRepository.toggleBookmark(
                            questionId = session.currentQuestion.id,
                            bookmarkedAt = Clock.System.now().toEpochMilliseconds()
                        )
                        currentQuestionBookmarked = userProgressRepository.isBookmarked(session.currentQuestion.id)
                    }
                },
                onExplain = { userAnswer, correctAnswer, isCorrect ->
                    navigate(Screen.AiExplanation(
                        questionText = session.currentQuestion.question,
                        userAnswer = userAnswer,
                        correctAnswer = correctAnswer,
                        isCorrect = isCorrect
                    ))
                }
            )
        }
        Screen.Bookmarks -> BookmarksScreen(
            userProgressRepository = userProgressRepository,
            onBack = { navigateBack() },
            onStartPractice = {
                coroutineScope.launch {
                    val bookmarked = questionRepository.getBookmarkedQuestionsForPractice()
                    if (bookmarked.isNotEmpty()) {
                        testSession = TestSession(questions = bookmarked.shuffled())
                        navigate(Screen.Question)
                    }
                }
            }
        )
        is Screen.AiExplanation -> AiExplanationScreen(
            questionText = screen.questionText,
            userAnswer = screen.userAnswer,
            correctAnswer = screen.correctAnswer,
            isCorrect = screen.isCorrect,
            aiAssistant = aiAssistant,
            onBack = { navigateBack() }
        )
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
                backStack.removeAt(backStack.lastIndex)
                backStack.add(Screen.Question)
            }
        )
    }
    } // AnimatedContent
}
