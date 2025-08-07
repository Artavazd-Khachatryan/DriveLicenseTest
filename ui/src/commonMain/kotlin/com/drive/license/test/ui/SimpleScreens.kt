package com.drive.license.test.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.database.repository.QuestionRepository
import com.drive.license.test.ui.components.QuestionScreen
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.collectAsState

@Composable
fun TestModeScreen(
    questionRepository: QuestionRepository,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val questions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())
    val viewModel = remember { QuestionViewModel(questionRepository, coroutineScope) }
    
    if (questions.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading questions...",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        QuestionScreen(
            viewModel = viewModel,
            modifier = modifier
        )
    }
}

@Composable
fun PracticeModeScreen(
    questionRepository: QuestionRepository,
    modifier: Modifier = Modifier
) {
    val questions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Practice Mode",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Total questions available: ${questions.size}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { }
        ) {
            Text("Start Practice")
        }
    }
}

@Composable
fun StatisticsScreen(
    questionRepository: QuestionRepository,
    modifier: Modifier = Modifier
) {
    val questions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Questions loaded: ${questions.size}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { }
        ) {
            Text("View Stats")
        }
    }
}

@Composable
fun AIAssistantScreen(
    questionRepository: QuestionRepository,
    modifier: Modifier = Modifier
) {
    val questions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "AI Assistant",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Questions available: ${questions.size}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { }
        ) {
            Text("Chat with AI")
        }
    }
} 