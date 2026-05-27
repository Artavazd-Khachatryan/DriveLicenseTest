package com.drive.license.test

import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import com.drive.license.test.database.DatabaseInitializer
import com.drive.license.test.domain.repository.AiAssistant
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.domain.repository.ReminderPreferences
import com.drive.license.test.domain.repository.ReminderScheduler
import com.drive.license.test.domain.repository.ThemePreferences
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.MainScreen
import com.drive.license.test.ui.theme.AppTheme
import com.drive.license.test.crash.CrashReporting
import com.drive.license.test.di.KoinHelper

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()

    val questionRepository: QuestionRepository = KoinHelper.get()
    val userProgressRepository: UserProgressRepository = KoinHelper.get()
    val aiAssistant: AiAssistant = KoinHelper.get()
    val reminderPreferences: ReminderPreferences = KoinHelper.get()
    val reminderScheduler: ReminderScheduler = KoinHelper.get()
    val themePreferences: ThemePreferences = KoinHelper.get()

    var isDarkTheme by remember { mutableStateOf(themePreferences.loadDarkTheme()) }
    fun setDarkTheme(dark: Boolean) {
        isDarkTheme = dark
        themePreferences.saveDarkTheme(dark)
    }

    val databaseInitializer = remember {
        DatabaseInitializer(questionRepository, coroutineScope)
    }

    LaunchedEffect(Unit) {
        runCatching { CrashReporting.initialize() }
            .onFailure { CrashReporting.recordException(it, "CrashReporting.initialize") }
        databaseInitializer.initializeDatabase()
    }

    AppTheme(darkTheme = isDarkTheme) {
        MainScreen(
            questionRepository = questionRepository,
            userProgressRepository = userProgressRepository,
            aiAssistant = aiAssistant,
            reminderPreferences = reminderPreferences,
            reminderScheduler = reminderScheduler,
            learningCenters = LearningCentersData.all,
            coroutineScope = coroutineScope,
            isDarkTheme = isDarkTheme,
            onDarkThemeChange = ::setDarkTheme,
        )
    }
}
