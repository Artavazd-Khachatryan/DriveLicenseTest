package com.drive.license.test.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppOutlinedButton
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
import drivelicensetest.ui.generated.resources.home_ready_title
import com.drive.license.test.ui.util.AdaptiveContentContainer
import com.drive.license.test.ui.util.estimatedTestMinutes
import com.drive.license.test.ui.util.homeMotivationMessage
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
import drivelicensetest.ui.generated.resources.home_unseen_questions
import drivelicensetest.ui.generated.resources.home_view_map_button
import drivelicensetest.ui.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    userStatistics: UserStatistics,
    unseenQuestionCount: Int = 0,
    totalQuestionCount: Int = 0,
    onStartTest: (Int) -> Unit,
    onOpenStats: () -> Unit,
    onOpenStatsFromRing: () -> Unit,
    onOpenFailed: () -> Unit,
    onOpenPractice: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenDrivingSchools: () -> Unit,
    onOpenSettings: () -> Unit,
    bottomBar: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTestLength by remember { mutableStateOf(20) }
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        contentVisible = true
    }

    AppScaffold(
        bottomBar = bottomBar,
        topBarActions = {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(Res.string.settings_title)
                )
            }
        }
    ) { inner ->
        AdaptiveContentContainer(
            modifier = modifier
                .fillMaxSize()
                .then(inner)
        ) { isExpanded, contentModifier ->
        val sectionSpacing = if (isExpanded) 16.dp else 24.dp
        val scrollState = rememberScrollState()
        LaunchedEffect(Unit) {
            scrollState.scrollTo(0)
        }
        Column(
            modifier = contentModifier
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(sectionSpacing)
        ) {
            val welcomeCard: @Composable () -> Unit = {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 }
            ) {
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.home_ready_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = homeMotivationMessage(userStatistics),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            }
            }

            val startTestCard: @Composable (Modifier) -> Unit = { cardModifier ->
            val chipColors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.onPrimary,
                selectedLabelColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                labelColor = MaterialTheme.colorScheme.onPrimary
            )
            AppCard(
                modifier = cardModifier.fillMaxWidth(),
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
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(Res.string.home_start_test_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        if (!isExpanded) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = homeMotivationMessage(userStatistics),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (totalQuestionCount > 0 && unseenQuestionCount > 0) {
                            Text(
                                text = stringResource(
                                    Res.string.home_unseen_questions,
                                    unseenQuestionCount,
                                    totalQuestionCount
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        Text(
                            text = stringResource(
                                Res.string.home_start_test_subtitle,
                                selectedTestLength,
                                estimatedTestMinutes(selectedTestLength)
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AppButton(
                            text = stringResource(Res.string.home_start_button),
                            onClick = { onStartTest(selectedTestLength) },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary,
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
                                    label = { Text(stringResource(Res.string.home_question_count, count)) },
                                    colors = chipColors
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        AppOutlinedButton(
                            text = stringResource(Res.string.home_practice_button),
                            onClick = onOpenPractice,
                            modifier = Modifier.fillMaxWidth(),
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            borderColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
            }

            val progressCard: @Composable (Modifier) -> Unit = { cardModifier ->
            HomeProgressCard(
                modifier = cardModifier,
                userStatistics = userStatistics,
                onOpenStatsFromRing = onOpenStatsFromRing
            )
            }

            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(300, delayMillis = 80))
            ) {
            if (isExpanded) {
                welcomeCard()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    startTestCard(Modifier.weight(1f))
                    progressCard(Modifier.weight(1f))
                }
            } else {
                startTestCard(Modifier)
            }
            }

            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(300, delayMillis = 160)) + slideInVertically(tween(300, delayMillis = 160)) { it / 4 }
            ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                // Review Mistakes - Consistent Size
                AppCard(
                    modifier = Modifier
                        .weight(1f)
                        .then(if (isExpanded) Modifier.height(200.dp) else Modifier.height(220.dp)),
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
                                Icon(
                                    Icons.Filled.Quiz,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
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
                if (AppFeatures.aiEnabled) AppCard(
                    modifier = Modifier
                        .weight(1f)
                        .then(if (isExpanded) Modifier.height(200.dp) else Modifier.height(220.dp)),
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
                                Icon(
                                    Icons.Filled.Chat,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
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
            }

            AnimatedVisibility(
                visible = contentVisible && AppFeatures.drivingSchoolsEnabled,
                enter = fadeIn(tween(300, delayMillis = 240)) + slideInVertically(tween(300, delayMillis = 240)) { it / 4 }
            ) {
            if (AppFeatures.drivingSchoolsEnabled) {
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
                            Icon(
                                Icons.Filled.School,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
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
                                onClick = onOpenDrivingSchools
                            )
                        }
                    }
                }
            }
            }

            if (!isExpanded) {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(300, delayMillis = 200))
                ) {
                    progressCard(Modifier)
                }
            }
        }
        }
    }
}

@Composable
private fun HomeProgressCard(
    userStatistics: UserStatistics,
    onOpenStatsFromRing: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.home_progress_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                        size = 120.dp,
                        contentDescription = stringResource(
                            Res.string.home_accuracy_ring_cd,
                            (accuracy * 100).toInt()
                        ),
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
                                value = stringResource(
                                    Res.string.home_streak_days,
                                    userStatistics.currentStreak
                                ),
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
}



