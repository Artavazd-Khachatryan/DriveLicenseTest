package com.drive.license.test.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.TestSessionAnswerReview
import com.drive.license.test.domain.model.TestSessionReview
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.ProgressRing
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.test_review_correct_answer
import drivelicensetest.ui.generated.resources.test_review_no_answers
import drivelicensetest.ui.generated.resources.test_review_not_found
import drivelicensetest.ui.generated.resources.test_review_question_label
import drivelicensetest.ui.generated.resources.test_review_summary
import drivelicensetest.ui.generated.resources.test_review_title
import drivelicensetest.ui.generated.resources.test_review_your_answer
import org.jetbrains.compose.resources.stringResource

@Composable
fun TestSessionReviewScreen(
    sessionId: String,
    userProgressRepository: UserProgressRepository,
    onBack: () -> Unit,
    onOpenQuestion: (answer: TestSessionAnswerReview, questionNumber: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    var review by remember { mutableStateOf<TestSessionReview?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var notFound by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        review = userProgressRepository.getTestSessionReview(sessionId)
        notFound = review == null
        isLoading = false
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.test_review_title),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        },
    ) { inner ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().then(inner),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (notFound || review == null) {
            Box(
                modifier = Modifier.fillMaxSize().then(inner).padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.test_review_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            val sessionReview = review!!
            AdaptiveContentContainer(
                modifier = modifier.fillMaxSize().then(inner),
            ) { _, contentModifier ->
                if (sessionReview.answers.isEmpty()) {
                    Box(
                        modifier = contentModifier.padding(16.dp),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        AppCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(Res.string.test_review_no_answers),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(20.dp),
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = contentModifier,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            SessionSummaryCard(review = sessionReview)
                        }
                        itemsIndexed(
                            sessionReview.answers,
                            key = { index, answer -> "${answer.questionId}-$index" },
                        ) { index, answer ->
                            SessionAnswerCard(
                                index = index + 1,
                                answer = answer,
                                onClick = { onOpenQuestion(answer, index + 1) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionSummaryCard(review: TestSessionReview) {
    val summary = review.summary
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProgressRing(
                progress = summary.score,
                size = 96.dp,
                strokeWidth = 10.dp,
            )
            Text(
                text = stringResource(
                    Res.string.test_review_summary,
                    summary.correctAnswers,
                    summary.totalQuestions,
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun SessionAnswerCard(
    index: Int,
    answer: TestSessionAnswerReview,
    onClick: () -> Unit,
) {
    val badgeColor = if (answer.isCorrect) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val badgeContentColor = if (answer.isCorrect) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { role = Role.Button },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = badgeColor,
                ) {
                    Text(
                        text = stringResource(Res.string.test_review_question_label, index),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeContentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = answer.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            AnswerReviewRow(
                icon = if (answer.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                label = stringResource(Res.string.test_review_your_answer),
                answer = answer.selectedAnswer,
                containerColor = if (answer.isCorrect) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                },
                contentColor = if (answer.isCorrect) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                },
                iconTint = if (answer.isCorrect) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
            if (!answer.isCorrect) {
                AnswerReviewRow(
                    icon = Icons.Default.CheckCircle,
                    label = stringResource(Res.string.test_review_correct_answer),
                    answer = answer.correctAnswer,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    iconTint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@Composable
private fun AnswerReviewRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    answer: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = iconTint,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f),
                )
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = contentColor,
                )
            }
        }
    }
}
