package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.ProgressRing

@Composable
fun HomeScreen(
    onStartTest: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenFailed: () -> Unit,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppScaffold(topBarTitle = "Home") { inner ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .then(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProgressRing(progress = 0.7f, size = 140.dp) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "70%", style = MaterialTheme.typography.headlineMedium)
                        Text(text = "Completed", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatItem(title = "Correct", value = "140")
                    StatItem(title = "Incorrect", value = "60")
                    StatItem(title = "Unseen", value = "40")
                }
            }

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(text = "Start 20-question Test", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    AppButton(text = "Start Test", onClick = onStartTest, modifier = Modifier.fillMaxWidth())
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                AppCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Statistics", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        AppButton(text = "Open", onClick = onOpenStats, modifier = Modifier.fillMaxWidth())
                    }
                }

                AppCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Failed Questions", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        AppButton(text = "Review", onClick = onOpenFailed, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(text = "Chat with Assistant", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ask explanations for rules and questions.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    AppButton(text = "Open Chat", onClick = onOpenChat, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun StatItem(title: String, value: String) {
    Column {
        Text(text = value, style = MaterialTheme.typography.titleLarge)
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


