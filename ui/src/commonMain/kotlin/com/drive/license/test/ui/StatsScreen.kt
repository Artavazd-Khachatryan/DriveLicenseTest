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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.CategoryStats
import com.drive.license.test.domain.model.TestSessionSummary
import com.drive.license.test.domain.model.UserStatistics
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.ProgressRing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun StatsScreen(
    userProgressRepository: UserProgressRepository,
    onBack: () -> Unit
) {
    var stats by remember { mutableStateOf(UserStatistics()) }
    var categoryStats by remember { mutableStateOf<List<CategoryStats>>(emptyList()) }
    var testHistory by remember { mutableStateOf<List<TestSessionSummary>>(emptyList()) }

    LaunchedEffect(Unit) {
        stats = userProgressRepository.getUserStatistics()
        categoryStats = userProgressRepository.getCategoryStats()
        testHistory = userProgressRepository.getTestHistory()
    }

    AppScaffold(
        topBarTitle = "Statistics",
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(inner)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 720.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OverallStatsCard(stats)
            CategoryBreakdownCard(categoryStats)
            TestHistoryCard(testHistory)
        }
    }
}

@Composable
private fun OverallStatsCard(stats: UserStatistics) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Overall Performance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProgressRing(
                    progress = stats.overallAccuracy,
                    size = 100.dp,
                    strokeWidth = 10.dp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatRow(label = "Total attempts", value = stats.totalAttempts.toString())
                    StatRow(label = "Correct", value = stats.totalCorrect.toString(), positive = true)
                    StatRow(label = "Incorrect", value = stats.totalIncorrect.toString(), positive = false)
                }
            }
        }
    }
}

@Composable
private fun CategoryBreakdownCard(categories: List<CategoryStats>) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "By Category",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (categories.isEmpty()) {
                Text(
                    text = "No data yet — complete a test to see category stats.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                categories.forEachIndexed { index, cat ->
                    CategoryRow(cat)
                    if (index < categories.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(cat: CategoryStats) {
    val displayName = cat.categoryName
        .replace("_", " ")
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (cat.attempted) {
                Text(
                    text = "${(cat.accuracy * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accuracyColor(cat.accuracy)
                )
            } else {
                Text(
                    text = "—",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (cat.attempted) {
            LinearProgressIndicator(
                progress = { cat.accuracy },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = accuracyColor(cat.accuracy),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun TestHistoryCard(history: List<TestSessionSummary>) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Test History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (history.isEmpty()) {
                Text(
                    text = "No completed tests yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                history.forEachIndexed { index, session ->
                    TestHistoryRow(session)
                    if (index < history.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun TestHistoryRow(session: TestSessionSummary) {
    val dateStr = remember(session.startTime) {
        val dt = Instant.fromEpochMilliseconds(session.startTime)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        "${dt.date.dayOfMonth}.${dt.date.monthNumber.toString().padStart(2, '0')}.${dt.date.year}"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = dateStr, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${session.correctAnswers}/${session.totalQuestions} correct",
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
                contentDescription = if (session.passed) "Passed" else "Failed",
                modifier = Modifier.size(18.dp),
                tint = if (session.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, positive: Boolean? = null) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = when (positive) {
                true -> MaterialTheme.colorScheme.primary
                false -> MaterialTheme.colorScheme.error
                null -> MaterialTheme.colorScheme.onSurface
            }
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun accuracyColor(accuracy: Float) = when {
    accuracy >= 0.8f -> MaterialTheme.colorScheme.primary
    accuracy >= 0.5f -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.error
}
