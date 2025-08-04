package com.drive.license.test

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.drive.license.test.database.Database
import com.drive.license.test.database.DatabaseInitializer
import com.drive.license.test.repository.QuestionRepository
import com.drive.license.test.ui.MainScreen
import androidx.compose.runtime.rememberCoroutineScope
import com.drive.license.test.database.getDatabaseDriverFactory

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()

    val databaseDriverFactory = getDatabaseDriverFactory()
    val database = remember { Database(databaseDriverFactory) }
    val questionRepository = remember { QuestionRepository(database) }

    val databaseInitializer = remember { DatabaseInitializer(questionRepository, coroutineScope) }
    LaunchedEffect(Unit) {
        databaseInitializer.initializeDatabase()
    }

    MaterialTheme {
        MainScreen(
            questionRepository = questionRepository,
            coroutineScope = coroutineScope
        )
    }
}
