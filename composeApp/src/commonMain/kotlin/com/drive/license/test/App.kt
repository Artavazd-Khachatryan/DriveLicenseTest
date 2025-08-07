package com.drive.license.test

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import com.drive.license.test.database.DatabaseInitializer
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.ui.MainScreen
import com.drive.license.test.di.getKoin

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    
    // ✅ Use Koin DI for clean dependency management
    val questionRepository: QuestionRepository = getKoin()
    
    // ✅ Create DatabaseInitializer manually since it needs CoroutineScope
    val databaseInitializer = remember { 
        DatabaseInitializer(questionRepository, coroutineScope) 
    }
    
    LaunchedEffect(Unit) {
        databaseInitializer.initializeDatabase()
    }
    
    // ✅ Pass domain interface to UI (not implementation)
    MaterialTheme {
        MainScreen(
            questionRepository = questionRepository,
            coroutineScope = coroutineScope
        )
    }
}
