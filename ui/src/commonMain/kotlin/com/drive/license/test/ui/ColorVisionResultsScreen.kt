package com.drive.license.test.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppOutlinedButton
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.ProgressRing
import com.drive.license.test.ui.components.StatChip
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.color_vision_disclaimer_short
import drivelicensetest.ui.generated.resources.color_vision_results_failed_subtitle
import drivelicensetest.ui.generated.resources.color_vision_results_failed_title
import drivelicensetest.ui.generated.resources.color_vision_results_passed_subtitle
import drivelicensetest.ui.generated.resources.color_vision_results_passed_title
import drivelicensetest.ui.generated.resources.color_vision_results_title
import drivelicensetest.ui.generated.resources.color_vision_retake
import drivelicensetest.ui.generated.resources.home_stat_correct
import drivelicensetest.ui.generated.resources.home_stat_incorrect
import drivelicensetest.ui.generated.resources.results_back_home
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun ColorVisionResultsScreen(
    session: ColorVisionSession,
    onBackToHome: () -> Unit,
    onRetake: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val total = session.plates.size
    val score = if (total > 0) session.correctAnswers.toFloat() / total else 0f
    val incorrect = total - session.correctAnswers
    val passed = session.demoPlatePassed && score >= 0.8f

    var heroVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        heroVisible = true
    }

    val accentColor = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    AppScaffold(
        topBarTitle = stringResource(Res.string.color_vision_results_title),
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = heroVisible,
                enter = fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.85f),
            ) {
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = if (passed) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        val onContainer = if (passed) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }

                        ProgressRing(
                            progress = score,
                            size = 120.dp,
                            strokeWidth = 12.dp,
                            progressColor = accentColor,
                            trackColor = onContainer.copy(alpha = 0.12f),
                        ) {
                            val animatedPercentage by animateIntAsState(
                                targetValue = (score * 100).toInt(),
                                animationSpec = tween(1200, easing = LinearEasing),
                                label = "color_vision_score",
                            )
                            Text(
                                text = "$animatedPercentage%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                            )
                        }

                        Text(
                            text = stringResource(
                                if (passed) {
                                    Res.string.color_vision_results_passed_title
                                } else {
                                    Res.string.color_vision_results_failed_title
                                },
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = onContainer,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(
                                if (passed) {
                                    Res.string.color_vision_results_passed_subtitle
                                } else {
                                    Res.string.color_vision_results_failed_subtitle
                                },
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = onContainer.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatChip(
                    label = stringResource(Res.string.home_stat_correct),
                    value = session.correctAnswers.toString(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                )
                StatChip(
                    label = stringResource(Res.string.home_stat_incorrect),
                    value = incorrect.toString(),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = stringResource(Res.string.color_vision_disclaimer_short),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            AppButton(
                text = stringResource(Res.string.color_vision_retake),
                onClick = onRetake,
                modifier = Modifier.fillMaxWidth(),
            )
            AppOutlinedButton(
                text = stringResource(Res.string.results_back_home),
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
