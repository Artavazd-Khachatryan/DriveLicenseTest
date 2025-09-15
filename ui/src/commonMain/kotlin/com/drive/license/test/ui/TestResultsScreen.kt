package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun TestResultsScreen(
    session: TestSession,
    onBackToHome: () -> Unit,
    onRetakeTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val score = session.correctAnswers.toFloat() / session.questions.size
    val passed = score >= 0.8f // 80% to pass
    
    AppScaffold(
        topBarTitle = "Test Results"
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .then(innerPadding)
                .padding(16.dp)
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Results Header
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = if (passed) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (passed) "Congratulations!" else "Keep Practicing!",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        color = if (passed) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (passed) "You passed the test!" else "You need 80% to pass",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = if (passed) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }
            }
            
            // Score Ring
            AppCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProgressRing(
                        progress = score,
                        size = 120.dp,
                        strokeWidth = 12.dp
                    )
                    
                    Text(
                        text = "Your Score",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Detailed Stats
            AppCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Correct Answers:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${session.correctAnswers}/${session.questions.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Incorrect Answers:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${session.questions.size - session.correctAnswers}/${session.questions.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Questions Answered:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${session.totalAnswered}/${session.questions.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    text = "Retake Test",
                    onClick = onRetakeTest,
                    modifier = Modifier.fillMaxWidth()
                )
                
                AppButton(
                    text = "Back to Home",
                    onClick = onBackToHome,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
