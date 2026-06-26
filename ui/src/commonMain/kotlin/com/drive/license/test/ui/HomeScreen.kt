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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.FeatureCard
import com.drive.license.test.ui.components.ProgressRing
import com.drive.license.test.ui.components.StatChip
import com.drive.license.test.ui.util.AdaptiveContentContainer
import com.drive.license.test.ui.util.estimatedTestMinutes
import com.drive.license.test.ui.util.homeMotivationMessage
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.home_color_vision_button
import drivelicensetest.ui.generated.resources.home_color_vision_subtitle
import drivelicensetest.ui.generated.resources.home_color_vision_title
import drivelicensetest.ui.generated.resources.home_learning_centers_subtitle
import drivelicensetest.ui.generated.resources.home_learning_centers_title
import drivelicensetest.ui.generated.resources.home_question_count
import drivelicensetest.ui.generated.resources.home_ready_title
import drivelicensetest.ui.generated.resources.home_start_button
import drivelicensetest.ui.generated.resources.home_start_test_subtitle
import drivelicensetest.ui.generated.resources.home_start_test_title
import drivelicensetest.ui.generated.resources.home_stat_learned
import drivelicensetest.ui.generated.resources.home_seen_ring_cd
import drivelicensetest.ui.generated.resources.home_stat_remaining
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
    onOpenStatsFromRing: () -> Unit,
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
        ) { _, contentModifier ->
            val sectionSpacing = 24.dp
            val scrollState = rememberScrollState()
            LaunchedEffect(Unit) { scrollState.scrollTo(0) }

            Column(
                modifier = contentModifier
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(sectionSpacing)
            ) {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 }
                ) {
                    HomeHeroCard(
                        modifier = Modifier.fillMaxWidth(),
                        userStatistics = userStatistics,
                        totalQuestionCount = totalQuestionCount,
                        onOpenStatsFromRing = onOpenStatsFromRing,
                    )
                }

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(300, delayMillis = 80)) +
                        slideInVertically(tween(300, delayMillis = 80)) { it / 4 }
                ) {
                    StartPracticeCard(
                        modifier = Modifier.fillMaxWidth(),
                        selectedTestLength = selectedTestLength,
                        onSelectLength = { selectedTestLength = it },
                        onStartTest = onStartTest,
                    )
                }

                if (onOpenColorVision != null) {
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(tween(300, delayMillis = 140)) +
                            slideInVertically(tween(300, delayMillis = 140)) { it / 4 }
                    ) {
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

                if (AppFeatures.drivingSchoolsEnabled) {
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(tween(300, delayMillis = 200)) +
                            slideInVertically(tween(300, delayMillis = 200)) { it / 4 }
                    ) {
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
                }
            }
        }
    }
}

@Composable
private fun HomeHeroCard(
    userStatistics: UserStatistics,
    totalQuestionCount: Int,
    onOpenStatsFromRing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coverageProgress = userStatistics.coverageProgress
    val totalCountOrNull = totalQuestionCount.takeIf { it > 0 }

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

                    key(userStatistics.questionsSeen, totalQuestionCount) {
                        val progress = if (totalCountOrNull != null) coverageProgress else 0f
                        HomeSeenProgressRing(
                            progress = progress,
                            seenCount = userStatistics.questionsSeen,
                            totalCount = totalCountOrNull,
                            contentDescription = if (totalCountOrNull != null) {
                                stringResource(
                                    Res.string.home_seen_ring_cd,
                                    (coverageProgress * 100).toInt(),
                                    userStatistics.questionsSeen,
                                    totalCountOrNull,
                                )
                            } else {
                                stringResource(Res.string.home_stat_seen)
                            },
                            onClick = onOpenStatsFromRing,
                        )
                    }
                }

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
            }
        }
    }
}

@Composable
private fun HomeSeenProgressRing(
    progress: Float,
    seenCount: Int,
    totalCount: Int?,
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
            if (totalCount != null && totalCount > 0) {
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
