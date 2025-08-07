package com.drive.license.test

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import com.drive.license.test.database.DatabaseInitializer
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.ui.MainScreen

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    
    // ✅ Use DI container for clean dependency management
    val questionRepository = remember { DiContainer.questionRepository }
    
    // ✅ Initialize database
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
