package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.MistakeQuestion
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.mistakes_correct_answer
import drivelicensetest.ui.generated.resources.mistakes_count
import drivelicensetest.ui.generated.resources.mistakes_empty_subtitle
import drivelicensetest.ui.generated.resources.mistakes_empty_title
import drivelicensetest.ui.generated.resources.mistakes_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReviewMistakesScreen(
    userProgressRepository: UserProgressRepository,
    onBack: () -> Unit
) {
    var mistakes by remember { mutableStateOf<List<MistakeQuestion>>(emptyList()) }

    LaunchedEffect(Unit) {
        mistakes = userProgressRepository.getMistakeQuestions()
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.mistakes_title),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (mistakes.isEmpty()) {
                EmptyMistakesCard()
            } else {
                Text(
                    text = stringResource(Res.string.mistakes_count, mistakes.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        mistakes.forEachIndexed { index, mistake ->
                            MistakeItem(mistake)
                            if (index < mistakes.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMistakesCard() {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun MistakeItem(mistake: MistakeQuestion) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = mistake.question,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(Res.string.mistakes_correct_answer),
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = mistake.correctAnswer,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
