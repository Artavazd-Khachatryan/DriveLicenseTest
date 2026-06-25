package com.drive.license.test.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.FeatureCard
import com.drive.license.test.ui.components.ProgressRing
import com.drive.license.test.ui.components.SectionHeader
import com.drive.license.test.ui.components.StatChip
import com.drive.license.test.ui.util.AdaptiveContentContainer
import com.drive.license.test.ui.util.estimatedTestMinutes
import com.drive.license.test.ui.util.homeMotivationMessage
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.home_learning_progress
import drivelicensetest.ui.generated.resources.home_learning_ring_cd
import drivelicensetest.ui.generated.resources.home_ai_assistant_subtitle
import drivelicensetest.ui.generated.resources.home_ai_assistant_title
import drivelicensetest.ui.generated.resources.home_chat_button
import drivelicensetest.ui.generated.resources.home_color_vision_button
import drivelicensetest.ui.generated.resources.home_color_vision_subtitle
import drivelicensetest.ui.generated.resources.home_color_vision_title
import drivelicensetest.ui.generated.resources.home_learning_centers_subtitle
import drivelicensetest.ui.generated.resources.home_learning_centers_title
import drivelicensetest.ui.generated.resources.home_question_count
import drivelicensetest.ui.generated.resources.home_quick_actions
import drivelicensetest.ui.generated.resources.home_ready_title
import drivelicensetest.ui.generated.resources.home_review_button
import drivelicensetest.ui.generated.resources.home_review_mistakes_subtitle
import drivelicensetest.ui.generated.resources.home_review_mistakes_title
import drivelicensetest.ui.generated.resources.home_start_button
import drivelicensetest.ui.generated.resources.home_start_test_subtitle
import drivelicensetest.ui.generated.resources.home_start_test_title
import drivelicensetest.ui.generated.resources.home_stat_learned
import drivelicensetest.ui.generated.resources.home_seen_ring_cd
import drivelicensetest.ui.generated.resources.home_stat_remaining
import drivelicensetest.ui.generated.resources.home_stat_attempted
import drivelicensetest.ui.generated.resources.home_stat_correct
import drivelicensetest.ui.generated.resources.home_stat_incorrect
import drivelicensetest.ui.generated.resources.home_stat_seen
import drivelicensetest.ui.generated.resources.home_streak_days
import drivelicensetest.ui.generated.resources.home_streak_label
import drivelicensetest.ui.generated.resources.home_view_map_button
import drivelicensetest.ui.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    userStatistics: UserStatistics,
    totalQuestionCount: Int = 0,
    onStartTest: (Int) -> Unit,
    onOpenStats: () -> Unit,
    onOpenStatsFromRing: () -> Unit,
    onOpenFailed: () -> Unit,
    mistakeCount: Int = 0,
    onOpenChat: () -> Unit,
    onOpenDrivingSchools: () -> Unit,
    onOpenColorVision: (() -> Unit)? = null,
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
            LaunchedEffect(Unit) { scrollState.scrollTo(0) }

            Column(
                modifier = contentModifier
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(sectionSpacing)
            ) {
                val heroCard: @Composable (Modifier) -> Unit = { cardModifier ->
                    HomeHeroCard(
                        modifier = cardModifier,
                        userStatistics = userStatistics,
                        totalQuestionCount = totalQuestionCount,
                        onOpenStatsFromRing = onOpenStatsFromRing,
                    )
                }

                val startTestCard: @Composable (Modifier) -> Unit = { cardModifier ->
                    StartPracticeCard(
                        modifier = cardModifier,
                        selectedTestLength = selectedTestLength,
                        onSelectLength = { selectedTestLength = it },
                        onStartTest = onStartTest,
                    )
                }

                val quickActions: @Composable (Modifier) -> Unit = { sectionModifier ->
                    Column(
                        modifier = sectionModifier,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SectionHeader(title = stringResource(Res.string.home_quick_actions))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (mistakeCount > 0) {
                                FeatureCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Filled.Quiz,
                                    title = stringResource(Res.string.home_review_mistakes_title),
                                    description = stringResource(Res.string.home_review_mistakes_subtitle),
                                    actionText = stringResource(Res.string.home_review_button),
                                    onAction = onOpenFailed,
                                    accent = MaterialTheme.colorScheme.error,
                                    onAccent = MaterialTheme.colorScheme.onError,
                                )
                            }
                            if (AppFeatures.aiEnabled) {
                                FeatureCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Filled.Chat,
                                    title = stringResource(Res.string.home_ai_assistant_title),
                                    description = stringResource(Res.string.home_ai_assistant_subtitle),
                                    actionText = stringResource(Res.string.home_chat_button),
                                    onAction = onOpenChat,
                                    accent = MaterialTheme.colorScheme.tertiary,
                                    onAccent = MaterialTheme.colorScheme.onTertiary,
                                )
                            }
                        }
                        if (AppFeatures.drivingSchoolsEnabled) {
                            FeatureCard(
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Filled.School,
                                title = stringResource(Res.string.home_learning_centers_title),
                                description = stringResource(Res.string.home_learning_centers_subtitle),
                                actionText = stringResource(Res.string.home_view_map_button),
                                onAction = onOpenDrivingSchools,
                                accent = MaterialTheme.colorScheme.secondary,
                                onAccent = MaterialTheme.colorScheme.onSecondary,
                            )
                        }
                        if (onOpenColorVision != null) {
                            FeatureCard(
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Filled.Palette,
                                title = stringResource(Res.string.home_color_vision_title),
                                description = stringResource(Res.string.home_color_vision_subtitle),
                                actionText = stringResource(Res.string.home_color_vision_button),
                                onAction = onOpenColorVision,
                                accent = MaterialTheme.colorScheme.tertiary,
                                onAccent = MaterialTheme.colorScheme.onTertiary,
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 }
                ) {
                    heroCard(Modifier.fillMaxWidth())
                }

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(300, delayMillis = 100)) +
                        slideInVertically(tween(300, delayMillis = 100)) { it / 4 }
                ) {
                    if (isExpanded) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            startTestCard(Modifier.weight(1f))
                            quickActions(Modifier.weight(1f))
                        }
                    } else {
                        startTestCard(Modifier.fillMaxWidth())
                    }
                }

                if (!isExpanded) {
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(tween(300, delayMillis = 180)) +
                            slideInVertically(tween(300, delayMillis = 180)) { it / 4 }
                    ) {
                        quickActions(Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

/**
 * Dashboard-style hero: greeting on the left, a circular "seen" ring on the right, a horizontal
 * "learned" bar below, and learned/remaining stat chips at the bottom.
 */
@Composable
private fun HomeHeroCard(
    userStatistics: UserStatistics,
    totalQuestionCount: Int,
    onOpenStatsFromRing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val learningProgress = userStatistics.learningProgress
    val coverageProgress = userStatistics.coverageProgress

    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.home_ready_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        if (AppFeatures.motivationEnabled) {
                            Text(
                                text = homeMotivationMessage(userStatistics),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            )
                        }
                        if (userStatistics.currentStreak > 0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            StreakPill(streak = userStatistics.currentStreak)
                        }
                    }

                    if (totalQuestionCount > 0) {
                        key(userStatistics.questionsSeen, totalQuestionCount) {
                            HomeSeenProgressRing(
                                progress = coverageProgress,
                                seenCount = userStatistics.questionsSeen,
                                totalCount = totalQuestionCount,
                                contentDescription = stringResource(
                                    Res.string.home_seen_ring_cd,
                                    (coverageProgress * 100).toInt(),
                                    userStatistics.questionsSeen,
                                    totalQuestionCount,
                                ),
                                onClick = onOpenStatsFromRing,
                            )
                        }
                    }
                }

                if (totalQuestionCount > 0) {
                    key(userStatistics.learnedQuestions, totalQuestionCount) {
                        HomeLearnedProgressBar(
                            progress = learningProgress,
                            learnedCount = userStatistics.learnedQuestions,
                            totalCount = totalQuestionCount,
                            contentDescription = stringResource(
                                Res.string.home_learning_ring_cd,
                                (learningProgress * 100).toInt(),
                                userStatistics.learnedQuestions,
                                totalQuestionCount,
                            ),
                            onClick = onOpenStatsFromRing,
                        )
                    }
                }

                if (totalQuestionCount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatChip(
                            label = stringResource(Res.string.home_stat_learned),
                            value = userStatistics.learnedQuestions.toString(),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = stringResource(Res.string.home_stat_remaining),
                            value = userStatistics.questionsRemaining.toString(),
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else if (userStatistics.totalAttempts > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatChip(
                            label = stringResource(Res.string.home_stat_correct),
                            value = userStatistics.totalCorrect.toString(),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeLearnedProgressBar(
    progress: Float,
    learnedCount: Int,
    totalCount: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visualProgress = if (learnedCount > 0) {
        progress.coerceIn(0f, 1f).coerceAtLeast(0.02f)
    } else {
        progress.coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = visualProgress,
        animationSpec = tween(1200, easing = LinearEasing),
        label = "home_learned_progress",
    )
    val animatedPercentage by animateIntAsState(
        targetValue = (progress * 100).toInt(),
        animationSpec = tween(1200, easing = LinearEasing),
        label = "home_learned_percentage",
    )
    val onContainer = MaterialTheme.colorScheme.onPrimaryContainer

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { this.contentDescription = contentDescription }
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.home_stat_learned),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = onContainer,
            )
            Text(
                text = "$animatedPercentage%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(MaterialTheme.shapes.small),
            color = MaterialTheme.colorScheme.primary,
            trackColor = onContainer.copy(alpha = 0.12f),
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Text(
            text = stringResource(
                Res.string.home_learning_progress,
                learnedCount,
                totalCount,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = onContainer.copy(alpha = 0.75f),
        )
    }
}

@Composable
private fun HomeSeenProgressRing(
    progress: Float,
    seenCount: Int,
    totalCount: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visualProgress = if (seenCount > 0) {
        progress.coerceIn(0f, 1f).coerceAtLeast(0.02f)
    } else {
        progress.coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = visualProgress,
        animationSpec = tween(1200, easing = LinearEasing),
        label = "home_seen_progress",
    )
    ProgressRing(
        progress = animatedProgress,
        size = 96.dp,
        strokeWidth = 10.dp,
        progressColor = MaterialTheme.colorScheme.tertiary,
        contentDescription = contentDescription,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val animatedCount by animateIntAsState(
                targetValue = seenCount,
                animationSpec = tween(1200, easing = LinearEasing),
                label = "home_seen_count",
            )
            Text(
                text = "$animatedCount",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
            )
            Text(
                text = stringResource(Res.string.home_stat_seen),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                maxLines = 1,
            )
            if (totalCount > 0) {
                Text(
                    text = "/ $totalCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun StreakPill(streak: Int) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.tertiary,
            )
            Text(
                text = stringResource(Res.string.home_streak_label) + " · " +
                    stringResource(Res.string.home_streak_days, streak),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/** Primary call to action: pick a test length and start. */
@Composable
private fun StartPracticeCard(
    selectedTestLength: Int,
    onSelectLength: (Int) -> Unit,
    onStartTest: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(10, 20, 30)
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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

                Spacer(modifier = Modifier.height(8.dp))

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

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    options.forEachIndexed { index, count ->
                        SegmentedButton(
                            selected = selectedTestLength == count,
                            onClick = { onSelectLength(count) },
                            shape = SegmentedButtonDefaults.itemShape(index, options.size),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.onPrimary,
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                inactiveContainerColor = MaterialTheme.colorScheme.primary,
                                inactiveContentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                            label = { Text(stringResource(Res.string.home_question_count, count)) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AppButton(
                    text = stringResource(Res.string.home_start_button),
                    onClick = { onStartTest(selectedTestLength) },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
