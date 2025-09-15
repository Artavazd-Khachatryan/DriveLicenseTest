package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.Question
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.AnswerButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward

@Composable
fun QuestionDetailScreen(
    question: Question,
    questionNumber: Int,
    totalQuestions: Int,
    selectedAnswer: String? = null,
    onBack: () -> Unit,
    onAnswer: (String) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAnswerIndex by remember(question.id) { 
        mutableStateOf(
            if (selectedAnswer != null) {
                question.answers.indexOf(selectedAnswer).takeIf { it >= 0 }
            } else null
        )
    }
    var showResult by remember(question.id) { mutableStateOf(selectedAnswer != null) }

    AppScaffold(
        topBarTitle = "Question $questionNumber of $totalQuestions",
        topBarActions = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .then(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            question.answers.forEachIndexed { index, answer ->
                AnswerButton(
                    answer = answer,
                    isSelected = selectedAnswerIndex == index,
                    isCorrect = showResult && answer == question.correctAnswer,
                    isIncorrect = showResult && selectedAnswerIndex == index && answer != question.correctAnswer,
                    onClick = {
                        if (!showResult) {
                            selectedAnswerIndex = index
                            showResult = true
                            onAnswer(answer)
                        }
                    }
                )
            }

            if (showResult) {
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = if (selectedAnswerIndex != null && question.answers[selectedAnswerIndex!!] == question.correctAnswer) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (selectedAnswerIndex != null && question.answers[selectedAnswerIndex!!] == question.correctAnswer) {
                                "Correct!"
                            } else {
                                "Incorrect"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selectedAnswerIndex != null && question.answers[selectedAnswerIndex!!] == question.correctAnswer) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Correct answer: ${question.correctAnswer}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = if (selectedAnswerIndex != null && question.answers[selectedAnswerIndex!!] == question.correctAnswer) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppButton(
                    text = "Previous",
                    onClick = onPrevious,
                    modifier = Modifier.weight(1f),
                    enabled = questionNumber > 1
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                AppButton(
                    text = if (questionNumber == totalQuestions) "Finish" else "Next",
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    enabled = showResult
                )
            }
        }
    }
}
