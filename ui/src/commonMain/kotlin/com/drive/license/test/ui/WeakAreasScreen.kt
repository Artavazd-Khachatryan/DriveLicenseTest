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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TrendingDown
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
import com.drive.license.test.domain.model.WeakAreaQuestion
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.StatChip
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.weak_areas_stat_correct
import drivelicensetest.ui.generated.resources.weak_areas_stat_incorrect
import drivelicensetest.ui.generated.resources.weak_areas_count_label
import drivelicensetest.ui.generated.resources.weak_areas_empty_subtitle
import drivelicensetest.ui.generated.resources.weak_areas_empty_title
import drivelicensetest.ui.generated.resources.weak_areas_practice_button
import drivelicensetest.ui.generated.resources.weak_areas_question_label
import drivelicensetest.ui.generated.resources.weak_areas_subtitle
import drivelicensetest.ui.generated.resources.weak_areas_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeakAreasScreen(
    questionRepository: QuestionRepository,
    onBack: () -> Unit,
    onPracticeWeakAreas: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var weakAreas by remember { mutableStateOf<List<WeakAreaQuestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        weakAreas = questionRepository.getWeakAreaQuestionsForReview()
        isLoading = false
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.weak_areas_title),
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
                if (weakAreas.isEmpty()) {
                    Box(
                        modifier = contentModifier.padding(16.dp),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        EmptyWeakAreasCard(modifier = Modifier.fillMaxWidth())
                    }
                } else {
                    LazyColumn(
                        modifier = contentModifier,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            WeakAreasSummaryCard(weakAreaCount = weakAreas.size)
                        }
                        item {
                            AppButton(
                                text = stringResource(Res.string.weak_areas_practice_button),
                                onClick = onPracticeWeakAreas,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        itemsIndexed(weakAreas, key = { _, item -> item.id }) { index, item ->
                            WeakAreaCard(
                                index = index + 1,
                                item = item,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeakAreasSummaryCard(weakAreaCount: Int) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f),
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(Res.string.weak_areas_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(Res.string.weak_areas_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f),
                )
            }
            StatChip(
                label = stringResource(Res.string.weak_areas_count_label),
                value = weakAreaCount.toString(),
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

@Composable
private fun WeakAreaCard(
    index: Int,
    item: WeakAreaQuestion,
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
                color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                Text(
                    text = stringResource(Res.string.weak_areas_question_label, index),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
            Text(
                text = item.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatChip(
                    label = stringResource(Res.string.weak_areas_stat_incorrect),
                    value = item.timesIncorrect.toString(),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f),
                )
                StatChip(
                    label = stringResource(Res.string.weak_areas_stat_correct),
                    value = item.timesCorrect.toString(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun EmptyWeakAreasCard(modifier: Modifier = Modifier) {
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
                text = stringResource(Res.string.weak_areas_empty_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = stringResource(Res.string.weak_areas_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}
