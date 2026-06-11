package com.drive.license.test.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppOutlinedButton
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.ProgressRing
import com.drive.license.test.ui.components.StatChip
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.home_stat_attempted
import drivelicensetest.ui.generated.resources.home_stat_correct
import drivelicensetest.ui.generated.resources.home_stat_incorrect
import drivelicensetest.ui.generated.resources.mistakes_title
import drivelicensetest.ui.generated.resources.results_back_home
import drivelicensetest.ui.generated.resources.results_failed_title
import drivelicensetest.ui.generated.resources.results_passed_title
import com.drive.license.test.ui.util.testResultMotivationMessage
import drivelicensetest.ui.generated.resources.results_retake
import drivelicensetest.ui.generated.resources.results_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun TestResultsScreen(
    session: TestSession,
    onBackToHome: () -> Unit,
    onRetakeTest: () -> Unit,
    onReviewMistakes: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val total = session.questions.size
    val score = if (total > 0) session.correctAnswers.toFloat() / total else 0f
    val incorrect = total - session.correctAnswers
    val passed = if (session.isExamMode) {
        incorrect <= TestSession.EXAM_MAX_MISTAKES
    } else {
        score >= 0.8f
    }

    var heroVisible by remember { mutableStateOf(false) }
    var statsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        heroVisible = true
        delay(180)
        statsVisible = true
    }

    val accentColor = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    AppScaffold(
        topBarTitle = stringResource(Res.string.results_title)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .then(innerPadding)
                .padding(16.dp)
                .widthIn(max = 720.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = heroVisible,
                enter = fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.85f)
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
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val onContainer = if (passed) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                        ProgressRing(
                            progress = if (heroVisible) score else 0f,
                            size = 156.dp,
                            strokeWidth = 14.dp,
                            progressColor = accentColor,
                            trackColor = onContainer.copy(alpha = 0.12f),
                        ) {
                            val animatedPct by animateIntAsState(
                                targetValue = if (heroVisible) (score * 100).toInt() else 0,
                                animationSpec = tween(900, easing = LinearEasing),
                                label = "result_pct"
                            )
                            Text(
                                text = "$animatedPct%",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                            )
                        }
                        Text(
                            text = if (passed) stringResource(Res.string.results_passed_title) else stringResource(Res.string.results_failed_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = onContainer,
                        )
                        if (AppFeatures.motivationEnabled) {
                            Text(
                                text = testResultMotivationMessage(score, passed),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = onContainer.copy(alpha = 0.85f),
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = statsVisible,
                enter = slideInVertically(tween(300)) { it / 3 } + fadeIn(tween(300))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatChip(
                        label = stringResource(Res.string.home_stat_correct),
                        value = session.correctAnswers.toString(),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label = stringResource(Res.string.home_stat_incorrect),
                        value = incorrect.toString(),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label = stringResource(Res.string.home_stat_attempted),
                        value = "${session.totalAnswered}/$total",
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onReviewMistakes != null && incorrect > 0) {
                    AppButton(
                        text = stringResource(Res.string.mistakes_title),
                        onClick = onReviewMistakes,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                    AppOutlinedButton(
                        text = stringResource(Res.string.results_retake),
                        onClick = onRetakeTest,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    AppButton(
                        text = stringResource(Res.string.results_retake),
                        onClick = onRetakeTest,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                AppOutlinedButton(
                    text = stringResource(Res.string.results_back_home),
                    onClick = onBackToHome,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
