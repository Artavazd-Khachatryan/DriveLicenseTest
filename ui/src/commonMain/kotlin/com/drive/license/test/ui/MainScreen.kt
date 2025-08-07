package com.drive.license.test.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.repository.QuestionRepository
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    questionRepository: QuestionRepository,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Driving License Test",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Test") },
                    label = { Text("Test") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Practice") },
                    label = { Text("Practice") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Stats") },
                    label = { Text("Stats") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "AI") },
                    label = { Text("AI") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> HomeScreen(
                modifier = Modifier.padding(paddingValues)
            )
            1 -> TestModeScreen(
                questionRepository = questionRepository,
                coroutineScope = coroutineScope,
                modifier = Modifier.padding(paddingValues)
            )
            2 -> PracticeModeScreen(
                questionRepository = questionRepository,
                modifier = Modifier.padding(paddingValues)
            )
            3 -> StatisticsScreen(
                questionRepository = questionRepository,
                modifier = Modifier.padding(paddingValues)
            )
            4 -> AIAssistantScreen(
                questionRepository = questionRepository,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}



@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Welcome section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Driving License Test",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Master the rules of the road with our comprehensive test preparation app",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                title = "Take a Test",
                description = "20 random questions to test your knowledge",
                icon = Icons.AutoMirrored.Filled.List,
                onClick = { }
            )
            
            QuickActionCard(
                title = "Practice Mode",
                description = "Focus on questions you need to improve",
                icon = Icons.Default.Edit,
                onClick = { }
            )
            
            QuickActionCard(
                title = "View Statistics",
                description = "Track your progress and performance",
                icon = Icons.Default.Info,
                onClick = { }
            )
            
            QuickActionCard(
                title = "AI Assistant",
                description = "Get help and explanations from AI",
                icon = Icons.Default.Info,
                onClick = { }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun QuickActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 