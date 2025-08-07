package com.drive.license.test

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import com.drive.license.test.database.DatabaseInitializer
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.ui.MainScreen
import org.koin.core.context.GlobalContext

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    
    // ✅ Use Koin DI for clean dependency management
    val questionRepository: QuestionRepository = GlobalContext.get().get()
    val databaseInitializer: DatabaseInitializer = GlobalContext.get().get()
    
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
