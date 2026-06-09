package com.drive.license.test.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.drive.license.test.domain.QuestionSelector
import com.drive.license.test.ui.util.InTestMotivationKind
import com.drive.license.test.ui.util.resolveInTestMotivationKind
import com.drive.license.test.ui.util.shouldShowInTestMotivation
import com.drive.license.test.domain.model.ColorVisionPlate
import com.drive.license.test.domain.model.LearningCenter
import com.drive.license.test.domain.model.Question
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.domain.repository.AiAssistant
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.domain.repository.ReminderPreferences
import com.drive.license.test.domain.repository.ReminderScheduler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.components.AppBottomBar
import com.drive.license.test.ui.components.BottomNavItem
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.nav_home
import drivelicensetest.ui.generated.resources.nav_practice
import drivelicensetest.ui.generated.resources.nav_stats
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(
    questionRepository: QuestionRepository,
    userProgressRepository: UserProgressRepository,
    aiAssistant: AiAssistant,
    reminderPreferences: ReminderPreferences,
    reminderScheduler: ReminderScheduler,
    learningCenters: List<LearningCenter> = emptyList(),
    colorVisionPlates: List<ColorVisionPlate> = emptyList(),
    coroutineScope: CoroutineScope,
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = backStack.last()
    val canGoBack = backStack.size > 1

    var testSession by remember { mutableStateOf<TestSession?>(null) }
    var colorVisionSession by remember { mutableStateOf<ColorVisionSession?>(null) }
    var userStatistics by remember { mutableStateOf(UserStatistics()) }
    var examRemainingSeconds by remember { mutableStateOf<Int?>(null) }
    var currentQuestionBookmarked by remember { mutableStateOf(false) }
    val allQuestions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())
    var questionAttemptCounts by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var inTestMilestoneKind by remember { mutableStateOf<InTestMotivationKind?>(null) }
    var inTestMilestoneAnsweredCount by remember { mutableStateOf(0) }

    fun clearInTestMilestone() {
        inTestMilestoneKind = null
        inTestMilestoneAnsweredCount = 0
    }

    suspend fun refreshUserProgress() {
        userStatistics = userProgressRepository.getUserStatistics()
        questionAttemptCounts = userProgressRepository.getQuestionAttemptCounts()
    }

    fun navigate(screen: Screen) {
        if (screen.isTopLevel) {
            backStack.clear()
        }
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    fun navigateToHome() {
        backStack.clear()
        backStack.add(Screen.Home)
        testSession = null
        colorVisionSession = null
        examRemainingSeconds = null
        clearInTestMilestone()
    }

    fun openColorVisionIntro() {
        if (AppFeatures.colorVisionTestEnabled && colorVisionPlates.isNotEmpty()) {
            navigate(Screen.ColorVisionIntro)
        }
    }

    fun startColorVisionTest() {
        colorVisionSession = ColorVisionSession(plates = colorVisionPlates)
        navigate(Screen.ColorVisionTest)
    }

    /** Mirrors each screen's toolbar back: Android system back + iOS edge-swipe. */
    fun handleSystemBack() {
        when (currentScreen) {
            Screen.Question -> {
                clearInTestMilestone()
                testSession = null
                examRemainingSeconds = null
                navigateBack()
            }
            Screen.ColorVisionTest -> {
                colorVisionSession = null
                navigateBack()
            }
            Screen.ColorVisionResults -> navigateToHome()
            Screen.Results -> navigateToHome()
            Screen.Settings -> {
                coroutineScope.launch { refreshUserProgress() }
                navigateBack()
            }
            Screen.Practice, Screen.Stats -> {
                if (backStack.size == 1) navigate(Screen.Home) else navigateBack()
            }
            else -> navigateBack()
        }
    }

    val interceptSystemBack = canGoBack
        || currentScreen is Screen.Question
        || currentScreen is Screen.Results
        || currentScreen is Screen.ColorVisionTest
        || currentScreen is Screen.ColorVisionResults
        || (currentScreen.isTopLevel && currentScreen !is Screen.Home && backStack.size == 1)

    BackHandler(enabled = interceptSystemBack) {
        handleSystemBack()
    }

    fun pickFromPool(pool: List<Question>, count: Int): List<Question> =
        QuestionSelector.selectForPractice(pool, count, questionAttemptCounts)

    fun startTest(pool: List<Question>, count: Int) {
        val selected = pickFromPool(pool, count)
        if (selected.isNotEmpty()) {
            clearInTestMilestone()
            testSession = TestSession(questions = selected)
            navigate(Screen.Question)
        }
    }

    val navHomeLabel = stringResource(Res.string.nav_home)
    val navPracticeLabel = stringResource(Res.string.nav_practice)
    val navStatsLabel = stringResource(Res.string.nav_stats)
    val bottomBar: @Composable () -> Unit = {
        AppBottomBar(
            listOf(
                BottomNavItem(navHomeLabel, Icons.Default.Home, currentScreen is Screen.Home) { navigate(Screen.Home) },
                BottomNavItem(navPracticeLabel, Icons.Default.FitnessCenter, currentScreen is Screen.Practice) { navigate(Screen.Practice) },
                BottomNavItem(navStatsLabel, Icons.Default.BarChart, currentScreen is Screen.Stats) { navigate(Screen.Stats) }
            )
        )
    }

    LaunchedEffect(Unit) {
        refreshUserProgress()
    }

    val unseenQuestionCount = QuestionSelector.countUnseen(allQuestions, questionAttemptCounts)

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
                    refreshUserProgress()
                }
                navigate(Screen.Results)
            }
        }
    }

    AnimatedContent(
        targetState = currentScreen,
        modifier = Modifier.fillMaxSize(),
        contentKey = { screen -> screen::class },
        transitionSpec = {
            // Fade avoids overlapping screens stealing touches on iOS during slide transitions.
            // Quick tween + snapped size transform keeps navigation feeling instant.
            val fadeSpec = tween<Float>(durationMillis = 140)
            (fadeIn(fadeSpec) togetherWith fadeOut(fadeSpec))
                .using(SizeTransform(clip = false) { _, _ -> snap() })
        },
        label = "screen_transition"
    ) { screen ->
    when (screen) {
        Screen.Home -> HomeScreen(
            userStatistics = userStatistics,
            unseenQuestionCount = unseenQuestionCount,
            totalQuestionCount = allQuestions.size,
            onStartTest = { count -> startTest(allQuestions, count) },
            onOpenStats = { navigate(Screen.Stats) },
            onOpenFailed = { navigate(Screen.Mistakes) },
            onOpenPractice = { navigate(Screen.Practice) },
            onOpenChat = { },
            onOpenDrivingSchools = {
                if (AppFeatures.drivingSchoolsEnabled) navigate(Screen.DrivingSchools)
            },
            onOpenColorVision = if (AppFeatures.colorVisionTestEnabled && colorVisionPlates.isNotEmpty()) {
                { openColorVisionIntro() }
            } else null,
            onOpenStatsFromRing = { navigate(Screen.Stats) },
            onOpenSettings = { navigate(Screen.Settings) },
            bottomBar = bottomBar,
            modifier = modifier
        )
        Screen.Stats -> StatsScreen(
            userProgressRepository = userProgressRepository,
            onBack = if (canGoBack) ({ navigateBack() }) else null,
            bottomBar = bottomBar
        )
        Screen.Mistakes -> ReviewMistakesScreen(
            userProgressRepository = userProgressRepository,
            onBack = { navigateBack() },
            onPracticeMistakes = {
                coroutineScope.launch {
                    val mistakeIds = userProgressRepository.getMistakeQuestions().map { it.id }.toSet()
                    val pool = allQuestions.filter { it.id in mistakeIds }
                    if (pool.isNotEmpty()) {
                        startTest(pool, minOf(pool.size, 20))
                    }
                }
            },
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
                            startTest(weak, 20)
                        }
                    }
                },
                onStartExam = {
                    val examQuestions = pickFromPool(allQuestions, TestSession.EXAM_QUESTION_COUNT)
                    if (examQuestions.isNotEmpty()) {
                        testSession = TestSession(questions = examQuestions, isExamMode = true)
                        navigate(Screen.Question)
                    }
                },
                onOpenBookmarks = { navigate(Screen.Bookmarks) },
                onOpenColorVision = if (AppFeatures.colorVisionTestEnabled && colorVisionPlates.isNotEmpty()) {
                    { openColorVisionIntro() }
                } else null,
                onBack = if (canGoBack) ({ navigateBack() }) else null,
                bottomBar = bottomBar
            )
        }
        Screen.CategoryPicker -> CategoryPickerScreen(
            categories = remember(allQuestions) { allQuestions.toCategoryInfoList() },
            userProgressRepository = userProgressRepository,
            onSelectCategory = { category ->
                coroutineScope.launch {
                    val pool = questionRepository.getQuestionsByCategory(category).first()
                    startTest(pool, 20)
                }
            },
            onBack = { navigateBack() }
        )
        Screen.Question -> {
            val session = testSession
            if (session == null) {
                LaunchedEffect(Unit) { navigateBack() }
            } else {
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
                isDarkTheme = isDarkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onBack = { handleSystemBack() },
                onAnswer = { answer ->
                    val updated = session.answerQuestion(answer)
                    testSession = updated
                    if (AppFeatures.motivationEnabled && shouldShowInTestMotivation(updated.totalAnswered)) {
                        inTestMilestoneKind = resolveInTestMotivationKind(
                            updated.totalAnswered,
                            updated.correctAnswers,
                        )
                        inTestMilestoneAnsweredCount = updated.totalAnswered
                    }
                    coroutineScope.launch {
                        userProgressRepository.saveQuestionAttempt(
                            sessionId = updated.sessionId,
                            questionId = updated.currentQuestion.id,
                            selectedAnswer = answer,
                            isCorrect = answer == updated.currentQuestion.correctAnswer,
                            attemptTime = Clock.System.now().toEpochMilliseconds()
                        )
                    }
                },
                milestoneKind = inTestMilestoneKind,
                milestoneAnsweredCount = inTestMilestoneAnsweredCount,
                onNext = {
                    clearInTestMilestone()
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
                            refreshUserProgress()
                        }
                        navigate(Screen.Results)
                    }
                },
                onPrevious = {
                    clearInTestMilestone()
                    testSession = session.previousQuestion()
                },
                onToggleBookmark = {
                    coroutineScope.launch {
                        userProgressRepository.toggleBookmark(
                            questionId = session.currentQuestion.id,
                            bookmarkedAt = Clock.System.now().toEpochMilliseconds()
                        )
                        currentQuestionBookmarked = userProgressRepository.isBookmarked(session.currentQuestion.id)
                    }
                },
                onExplain = if (AppFeatures.aiEnabled) {
                    { userAnswer, correctAnswer, isCorrect ->
                        navigate(Screen.AiExplanation(
                            questionText = session.currentQuestion.question,
                            userAnswer = userAnswer,
                            correctAnswer = correctAnswer,
                            isCorrect = isCorrect
                        ))
                    }
                } else null
            )
            }
        }
        Screen.Bookmarks -> BookmarksScreen(
            userProgressRepository = userProgressRepository,
            onBack = { navigateBack() },
            onStartPractice = {
                coroutineScope.launch {
                    val bookmarked = questionRepository.getBookmarkedQuestionsForPractice()
                    if (bookmarked.isNotEmpty()) {
                        startTest(bookmarked, bookmarked.size)
                    }
                }
            }
        )
        Screen.DrivingSchools -> if (AppFeatures.drivingSchoolsEnabled) {
            DrivingSchoolsScreen(
                schools = learningCenters,
                onBack = { navigateBack() }
            )
        } else {
            LaunchedEffect(Unit) { navigateBack() }
        }
        Screen.ColorVisionIntro -> if (AppFeatures.colorVisionTestEnabled && colorVisionPlates.isNotEmpty()) {
            ColorVisionIntroScreen(
                plateCount = colorVisionPlates.size,
                onStart = { startColorVisionTest() },
                onBack = { navigateBack() },
            )
        } else {
            LaunchedEffect(Unit) { navigateBack() }
        }
        Screen.ColorVisionTest -> {
            val session = colorVisionSession
            if (session == null) {
                LaunchedEffect(Unit) { navigateBack() }
            } else {
                ColorVisionPlateScreen(
                    plate = session.currentPlate,
                    plateNumber = session.currentIndex + 1,
                    totalPlates = session.plates.size,
                    selectedAnswer = session.answers[session.currentIndex],
                    onBack = { handleSystemBack() },
                    onAnswer = { answer ->
                        colorVisionSession = session.answerPlate(answer)
                    },
                    onNext = {
                        val current = colorVisionSession ?: return@ColorVisionPlateScreen
                        val next = current.nextPlate()
                        colorVisionSession = next
                        if (next.isCompleted) {
                            navigate(Screen.ColorVisionResults)
                        }
                    },
                    onPrevious = {
                        colorVisionSession = colorVisionSession?.previousPlate()
                    },
                )
            }
        }
        Screen.ColorVisionResults -> {
            val session = colorVisionSession
            if (session == null) {
                LaunchedEffect(Unit) { navigateToHome() }
            } else {
                ColorVisionResultsScreen(
                    session = session,
                    onBackToHome = { navigateToHome() },
                    onRetake = {
                        colorVisionSession = ColorVisionSession(plates = colorVisionPlates)
                        if (backStack.last() is Screen.ColorVisionResults) {
                            backStack[backStack.lastIndex] = Screen.ColorVisionTest
                        } else {
                            navigate(Screen.ColorVisionTest)
                        }
                    },
                )
            }
        }
        Screen.Settings -> SettingsScreen(
            reminderPreferences = reminderPreferences,
            reminderScheduler = reminderScheduler,
            userProgressRepository = userProgressRepository,
            isDarkTheme = isDarkTheme,
            onDarkThemeChange = onDarkThemeChange,
            onBack = { handleSystemBack() },
            onStatisticsReset = {
                coroutineScope.launch { refreshUserProgress() }
            },
        )
        is Screen.AiExplanation -> AiExplanationScreen(
            questionText = screen.questionText,
            userAnswer = screen.userAnswer,
            correctAnswer = screen.correctAnswer,
            isCorrect = screen.isCorrect,
            aiAssistant = aiAssistant,
            onBack = { navigateBack() }
        )
        Screen.Results -> {
            val session = testSession
            if (session == null) {
                LaunchedEffect(Unit) {
                    backStack.clear()
                    backStack.add(Screen.Home)
                }
            } else {
                TestResultsScreen(
                    session = session,
                    onReviewMistakes = { navigate(Screen.Mistakes) },
                    onBackToHome = { navigateToHome() },
                    onRetakeTest = {
                        val count = session.questions.size
                        val selected = pickFromPool(allQuestions, count)
                        if (selected.isNotEmpty()) {
                            clearInTestMilestone()
                            testSession = TestSession(
                                questions = selected,
                                isExamMode = session.isExamMode,
                            )
                            if (session.isExamMode) {
                                examRemainingSeconds = TestSession.EXAM_DURATION_SECONDS
                            } else {
                                examRemainingSeconds = null
                            }
                            if (backStack.last() is Screen.Results) {
                                backStack[backStack.lastIndex] = Screen.Question
                            } else {
                                navigate(Screen.Question)
                            }
                        }
                    },
                )
            }
        }
    }
    } // AnimatedContent
}
