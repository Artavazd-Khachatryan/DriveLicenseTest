package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.CircularProgressIndicator
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
import com.drive.license.test.domain.repository.AiAssistant
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.ai_explain_correct_badge
import drivelicensetest.ui.generated.resources.ai_explain_correct_label
import drivelicensetest.ui.generated.resources.ai_explain_incorrect_badge
import drivelicensetest.ui.generated.resources.ai_explain_loading
import drivelicensetest.ui.generated.resources.ai_explain_title
import drivelicensetest.ui.generated.resources.ai_explain_your_answer_label
import drivelicensetest.ui.generated.resources.back
import org.jetbrains.compose.resources.stringResource

@Composable
fun AiExplanationScreen(
    questionText: String,
    userAnswer: String,
    correctAnswer: String,
    isCorrect: Boolean,
    aiAssistant: AiAssistant,
    onBack: () -> Unit
) {
    var explanation by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        explanation = aiAssistant.explainAnswer(questionText, userAnswer, correctAnswer, isCorrect)
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.ai_explain_title),
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = questionText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            val resultColor = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            val resultContainerColor = if (isCorrect) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
            val resultOnContainerColor = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer

            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = resultContainerColor
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = resultColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isCorrect) stringResource(Res.string.ai_explain_correct_badge)
                            else stringResource(Res.string.ai_explain_incorrect_badge),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = resultColor
                        )
                    }
                    if (!isCorrect) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = stringResource(Res.string.ai_explain_your_answer_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = resultOnContainerColor.copy(alpha = 0.7f)
                            )
                            Text(
                                text = userAnswer,
                                style = MaterialTheme.typography.bodyMedium,
                                color = resultOnContainerColor
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(Res.string.ai_explain_correct_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = resultOnContainerColor.copy(alpha = 0.7f)
                        )
                        Text(
                            text = correctAnswer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = resultOnContainerColor
                        )
                    }
                }
            }

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (explanation == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            Text(
                                text = stringResource(Res.string.ai_explain_loading),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = explanation!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
