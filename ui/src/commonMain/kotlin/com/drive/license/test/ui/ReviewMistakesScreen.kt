package com.drive.license.test.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.MistakeQuestion
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.StatChip
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.mistakes_correct_answer
import drivelicensetest.ui.generated.resources.mistakes_count_label
import drivelicensetest.ui.generated.resources.mistakes_empty_subtitle
import drivelicensetest.ui.generated.resources.mistakes_empty_title
import drivelicensetest.ui.generated.resources.mistakes_practice_button
import drivelicensetest.ui.generated.resources.mistakes_question_label
import drivelicensetest.ui.generated.resources.mistakes_subtitle
import drivelicensetest.ui.generated.resources.mistakes_title
import drivelicensetest.ui.generated.resources.mistakes_your_answer
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReviewMistakesScreen(
    userProgressRepository: UserProgressRepository,
    onBack: () -> Unit,
    onPracticeMistakes: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var mistakes by remember { mutableStateOf<List<MistakeQuestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        mistakes = userProgressRepository.getMistakeQuestions()
        isLoading = false
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.mistakes_title),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        }
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
                modifier = modifier.fillMaxSize().then(inner)
            ) { _, contentModifier ->
                if (mistakes.isEmpty()) {
                    Box(
                        modifier = contentModifier.padding(16.dp),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        EmptyMistakesCard(modifier = Modifier.fillMaxWidth())
                    }
                } else {
                    LazyColumn(
                        modifier = contentModifier,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            MistakesSummaryCard(mistakeCount = mistakes.size)
                        }
                        item {
                            AppButton(
                                text = stringResource(Res.string.mistakes_practice_button),
                                onClick = onPracticeMistakes,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        itemsIndexed(mistakes, key = { _, mistake -> mistake.id }) { index, mistake ->
                            MistakeCard(
                                index = index + 1,
                                mistake = mistake,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MistakesSummaryCard(mistakeCount: Int) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.18f),
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(Res.string.mistakes_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(Res.string.mistakes_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f),
                )
            }
            StatChip(
                label = stringResource(Res.string.mistakes_count_label),
                value = mistakeCount.toString(),
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

@Composable
private fun MistakeCard(
    index: Int,
    mistake: MistakeQuestion,
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.errorContainer,
            ) {
                Text(
                    text = stringResource(Res.string.mistakes_question_label, index),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
            Text(
                text = mistake.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            if (mistake.userAnswer.isNotBlank()) {
                AnswerCompareRow(
                    icon = Icons.Default.Cancel,
                    label = stringResource(Res.string.mistakes_your_answer),
                    answer = mistake.userAnswer,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    iconTint = MaterialTheme.colorScheme.error,
                )
            }
            AnswerCompareRow(
                icon = Icons.Default.CheckCircle,
                label = stringResource(Res.string.mistakes_correct_answer),
                answer = mistake.correctAnswer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                iconTint = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun AnswerCompareRow(
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

@Composable
private fun EmptyMistakesCard(modifier: Modifier = Modifier) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(Res.string.mistakes_empty_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = stringResource(Res.string.mistakes_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}
