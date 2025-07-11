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
import com.drive.license.test.repository.QuestionRepository
import kotlinx.coroutines.CoroutineScope

@Composable
fun TestModeScreen(
    questionRepository: QuestionRepository,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.List,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Test Mode",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Take a 20-question test to assess your knowledge",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { }
        ) {
            Text("Start Test")
        }
    }
}

@Composable
fun PracticeModeScreen(
    questionRepository: QuestionRepository,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
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
            text = "Focus on questions you need to improve",
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
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
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
            text = "Track your progress and performance",
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
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
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
            text = "Get help and explanations from AI",
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