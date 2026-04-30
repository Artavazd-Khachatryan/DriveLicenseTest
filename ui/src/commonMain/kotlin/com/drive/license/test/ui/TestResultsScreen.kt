package com.drive.license.test.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppOutlinedButton
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.ProgressRing
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.results_back_home
import drivelicensetest.ui.generated.resources.results_correct_answers
import drivelicensetest.ui.generated.resources.results_failed_subtitle
import drivelicensetest.ui.generated.resources.results_failed_title
import drivelicensetest.ui.generated.resources.results_incorrect_answers
import drivelicensetest.ui.generated.resources.results_passed_subtitle
import drivelicensetest.ui.generated.resources.results_passed_title
import drivelicensetest.ui.generated.resources.results_questions_answered
import drivelicensetest.ui.generated.resources.results_retake
import drivelicensetest.ui.generated.resources.results_title
import drivelicensetest.ui.generated.resources.results_your_score
import org.jetbrains.compose.resources.stringResource

@Composable
fun TestResultsScreen(
    session: TestSession,
    onBackToHome: () -> Unit,
    onRetakeTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val score = session.correctAnswers.toFloat() / session.questions.size
    val passed = score >= 0.8f

    var headerVisible by remember { mutableStateOf(false) }
    var ringVisible by remember { mutableStateOf(false) }
    var statsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        headerVisible = true
        delay(150)
        ringVisible = true
        delay(100)
        statsVisible = true
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.results_title)
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
            
            AnimatedVisibility(
                visible = headerVisible,
                enter = slideInVertically(animationSpec = tween(300)) { -it / 2 } + fadeIn(tween(300))
            ) {
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
                            text = if (passed) stringResource(Res.string.results_passed_title) else stringResource(Res.string.results_failed_title),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = if (passed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (passed) stringResource(Res.string.results_passed_subtitle) else stringResource(Res.string.results_failed_subtitle),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = if (passed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = ringVisible,
                enter = fadeIn(tween(400))
            ) {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProgressRing(
                            progress = if (ringVisible) score else 0f,
                            size = 120.dp,
                            strokeWidth = 12.dp
                        )
                        Text(
                            text = stringResource(Res.string.results_your_score),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = statsVisible,
                enter = slideInVertically(animationSpec = tween(300)) { it / 2 } + fadeIn(tween(300))
            ) {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(Res.string.results_correct_answers),
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
                            text = stringResource(Res.string.results_incorrect_answers),
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
                            text = stringResource(Res.string.results_questions_answered),
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
            } // AnimatedVisibility stats

            Spacer(modifier = Modifier.weight(1f))
            
            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    text = stringResource(Res.string.results_retake),
                    onClick = onRetakeTest,
                    modifier = Modifier.fillMaxWidth()
                )

                AppOutlinedButton(
                    text = stringResource(Res.string.results_back_home),
                    onClick = onBackToHome,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
