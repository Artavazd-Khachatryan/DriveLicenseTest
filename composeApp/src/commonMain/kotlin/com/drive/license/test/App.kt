package com.drive.license.test

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import com.drive.license.test.database.DatabaseInitializer
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.ui.MainScreen
import com.drive.license.test.di.KoinHelper

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    
    val questionRepository: QuestionRepository = KoinHelper.get()
    
    val databaseInitializer = remember {
        DatabaseInitializer(questionRepository, coroutineScope) 
    }
    
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
