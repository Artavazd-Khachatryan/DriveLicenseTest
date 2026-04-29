package com.drive.license.test.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.ProgressRing
import com.drive.license.test.ui.components.StatChip
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.home_accuracy
import drivelicensetest.ui.generated.resources.home_ai_assistant_subtitle
import drivelicensetest.ui.generated.resources.home_ai_assistant_title
import drivelicensetest.ui.generated.resources.home_chat_button
import drivelicensetest.ui.generated.resources.home_learning_centers_subtitle
import drivelicensetest.ui.generated.resources.home_learning_centers_title
import drivelicensetest.ui.generated.resources.home_progress_title
import drivelicensetest.ui.generated.resources.home_ready_subtitle
import drivelicensetest.ui.generated.resources.home_ready_title
import drivelicensetest.ui.generated.resources.home_review_button
import drivelicensetest.ui.generated.resources.home_review_mistakes_subtitle
import drivelicensetest.ui.generated.resources.home_review_mistakes_title
import drivelicensetest.ui.generated.resources.home_practice_button
import drivelicensetest.ui.generated.resources.home_start_button
import drivelicensetest.ui.generated.resources.home_start_test_subtitle
import drivelicensetest.ui.generated.resources.home_start_test_title
import drivelicensetest.ui.generated.resources.home_stat_attempted
import drivelicensetest.ui.generated.resources.home_stat_correct
import drivelicensetest.ui.generated.resources.home_stat_incorrect
import drivelicensetest.ui.generated.resources.home_accuracy_ring_cd
import drivelicensetest.ui.generated.resources.home_question_count
import drivelicensetest.ui.generated.resources.home_streak_days
import drivelicensetest.ui.generated.resources.home_streak_label
import drivelicensetest.ui.generated.resources.home_test_length_label
import drivelicensetest.ui.generated.resources.home_view_map_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    userStatistics: UserStatistics,
    onStartTest: (Int) -> Unit,
    onOpenStats: () -> Unit,
    onOpenStatsFromRing: () -> Unit,
    onOpenFailed: () -> Unit,
    onOpenPractice: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenMap: () -> Unit,
    bottomBar: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTestLength by remember { mutableStateOf(20) }

    AppScaffold(bottomBar = bottomBar) { inner ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .then(inner)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 720.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Welcome Section - Clean and Readable
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🚗",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.home_ready_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.home_ready_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 2. Enhanced Primary Action - Start Test with Animations
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .padding(28.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = stringResource(Res.string.home_start_test_title),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = stringResource(Res.string.home_start_test_subtitle),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(Res.string.home_test_length_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(10, 20, 30).forEach { count ->
                                FilterChip(
                                    selected = selectedTestLength == count,
                                    onClick = { selectedTestLength = count },
                                    label = { Text(stringResource(Res.string.home_question_count, count)) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val buttonScale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(dampingRatio = 0.8f),
                            label = "button_scale"
                        )

                        AppButton(
                            text = stringResource(Res.string.home_start_button),
                            onClick = { onStartTest(selectedTestLength) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { scaleX = buttonScale; scaleY = buttonScale }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AppButton(
                            text = stringResource(Res.string.home_practice_button),
                            onClick = onOpenPractice,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(Res.string.home_progress_title),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Centered Progress Ring
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val accuracy = userStatistics.overallAccuracy
                            val animatedProgress by animateFloatAsState(
                                targetValue = accuracy,
                                animationSpec = tween(1500, easing = LinearEasing),
                                label = "progress"
                            )

                            ProgressRing(
                                progress = animatedProgress,
                                size = 140.dp,
                                contentDescription = stringResource(Res.string.home_accuracy_ring_cd, (accuracy * 100).toInt()),
                                modifier = Modifier.clickable { onOpenStatsFromRing() }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val animatedPercentage by animateIntAsState(
                                        targetValue = (accuracy * 100).toInt(),
                                        animationSpec = tween(1500, easing = LinearEasing),
                                        label = "percentage"
                                    )
                                    Text(
                                        text = "$animatedPercentage%",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = stringResource(Res.string.home_accuracy),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (userStatistics.totalAttempts > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatChip(
                                    label = stringResource(Res.string.home_stat_correct),
                                    value = userStatistics.totalCorrect.toString(),
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                StatChip(
                                    label = stringResource(Res.string.home_stat_incorrect),
                                    value = userStatistics.totalIncorrect.toString(),
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                StatChip(
                                    label = stringResource(Res.string.home_stat_attempted),
                                    value = userStatistics.totalAttempts.toString(),
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                if (userStatistics.currentStreak > 0) {
                                    StatChip(
                                        label = stringResource(Res.string.home_streak_label),
                                        value = stringResource(Res.string.home_streak_days, userStatistics.currentStreak),
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                // Review Mistakes - Consistent Size
                AppCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Animated Icon
                                val iconScale by animateFloatAsState(
                                    targetValue = 1f,
                                    animationSpec = spring(dampingRatio = 0.7f),
                                    label = "review_icon_scale"
                                )
                                
                                Icon(
                                    Icons.Filled.Quiz,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .graphicsLayer { scaleX = iconScale; scaleY = iconScale },
                                    tint = MaterialTheme.colorScheme.error
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = stringResource(Res.string.home_review_mistakes_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = stringResource(Res.string.home_review_mistakes_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            AppButton(
                                text = stringResource(Res.string.home_review_button),
                                onClick = onOpenFailed,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                
                // AI Assistant - Consistent Size
                AppCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp), // Fixed height for consistency
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f),
                                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Animated Icon
                                val iconScale by animateFloatAsState(
                                    targetValue = 1f,
                                    animationSpec = spring(dampingRatio = 0.7f),
                                    label = "chat_icon_scale"
                                )
                                
                                Icon(
                                    Icons.Filled.Chat,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .graphicsLayer { scaleX = iconScale; scaleY = iconScale },
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = stringResource(Res.string.home_ai_assistant_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = stringResource(Res.string.home_ai_assistant_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            AppButton(
                                text = stringResource(Res.string.home_chat_button),
                                onClick = onOpenChat,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // 5. Enhanced Learning Places with Animation
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Animated School Icon
                        val iconScale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(dampingRatio = 0.8f),
                            label = "school_icon_scale"
                        )
                        
                        Icon(
                            Icons.Filled.School,
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .graphicsLayer { scaleX = iconScale; scaleY = iconScale },
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.home_learning_centers_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.home_learning_centers_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        AppButton(
                            text = stringResource(Res.string.home_view_map_button),
                            onClick = onOpenMap
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.headlineSmall)
}


