package com.drive.license.test.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.CategoryStats
import com.drive.license.test.domain.model.TestSessionSummary
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.MasteryRow
import com.drive.license.test.ui.components.ProgressRing
import com.drive.license.test.ui.components.StatChip
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.stats_by_category
import drivelicensetest.ui.generated.resources.stats_correct
import drivelicensetest.ui.generated.resources.stats_correct_of_total
import drivelicensetest.ui.generated.resources.stats_failed
import drivelicensetest.ui.generated.resources.stats_incorrect
import drivelicensetest.ui.generated.resources.stats_no_category_data
import drivelicensetest.ui.generated.resources.stats_no_history
import drivelicensetest.ui.generated.resources.stats_overall_performance
import drivelicensetest.ui.generated.resources.stats_passed
import drivelicensetest.ui.generated.resources.stats_test_history
import drivelicensetest.ui.generated.resources.stats_tap_to_review
import drivelicensetest.ui.generated.resources.stats_title
import drivelicensetest.ui.generated.resources.stats_total_attempts
import com.drive.license.test.ui.util.AdaptiveContentContainer
import com.drive.license.test.ui.util.formatCategoryName
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatsScreen(
    userProgressRepository: UserProgressRepository,
    onOpenSessionReview: (String) -> Unit = {},
    onBack: (() -> Unit)? = null,
) {
    var stats by remember { mutableStateOf(UserStatistics()) }
    var categoryStats by remember { mutableStateOf<List<CategoryStats>>(emptyList()) }
    var testHistory by remember { mutableStateOf<List<TestSessionSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        stats = userProgressRepository.getUserStatistics()
        categoryStats = userProgressRepository.getCategoryStats()
        testHistory = userProgressRepository.getTestHistory()
        isLoading = false
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.stats_title),
        navigationIcon = onBack?.let {
            {
                AppBackNavigationIcon(
                    onClick = it,
                    contentDescription = stringResource(Res.string.back),
                )
            }
        },
    ) { inner ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().then(inner),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            AdaptiveContentContainer(
                modifier = Modifier
                    .fillMaxSize()
                    .then(inner)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) { _, contentModifier ->
                Column(
                    modifier = contentModifier,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OverallStatsCard(stats)
                    CategoryBreakdownCard(categoryStats)
                    TestHistoryCard(
                        history = testHistory,
                        onOpenSessionReview = onOpenSessionReview,
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallStatsCard(stats: UserStatistics) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.stats_overall_performance),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            ProgressRing(
                progress = stats.overallAccuracy,
                size = 132.dp,
                strokeWidth = 12.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(
                    label = stringResource(Res.string.stats_correct),
                    value = stats.totalCorrect.toString(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = stringResource(Res.string.stats_incorrect),
                    value = stats.totalIncorrect.toString(),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = stringResource(Res.string.stats_total_attempts),
                    value = stats.totalAttempts.toString(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownCard(categories: List<CategoryStats>) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(Res.string.stats_by_category),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (categories.isEmpty()) {
                Text(
                    text = stringResource(Res.string.stats_no_category_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                categories.forEachIndexed { index, cat ->
                    MasteryRow(
                        label = formatCategoryName(cat.categoryName),
                        accuracy = cat.accuracy,
                        attempted = cat.attempted,
                    )
                    if (index < categories.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun TestHistoryCard(
    history: List<TestSessionSummary>,
    onOpenSessionReview: (String) -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.stats_test_history),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (history.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.stats_tap_to_review),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (history.isEmpty()) {
                Text(
                    text = stringResource(Res.string.stats_no_history),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                history.forEachIndexed { index, session ->
                    TestHistoryRow(
                        session = session,
                        onClick = { onOpenSessionReview(session.id) },
                    )
                    if (index < history.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun TestHistoryRow(
    session: TestSessionSummary,
    onClick: () -> Unit,
) {
    val dateStr = remember(session.startTime) {
        val dt = Instant.fromEpochMilliseconds(session.startTime)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        "${dt.date.dayOfMonth}.${dt.date.monthNumber.toString().padStart(2, '0')}.${dt.date.year}"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { role = Role.Button }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = dateStr, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = stringResource(Res.string.stats_correct_of_total, session.correctAnswers, session.totalQuestions),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${(session.score * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (session.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Icon(
                imageVector = if (session.passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = if (session.passed) stringResource(Res.string.stats_passed) else stringResource(Res.string.stats_failed),
                modifier = Modifier.size(18.dp),
                tint = if (session.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

