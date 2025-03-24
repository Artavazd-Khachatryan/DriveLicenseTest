package com.drive.license.test.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.drive.license.test.models.Question
import drivelicensetest.composeapp.generated.resources.Res
import drivelicensetest.composeapp.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun QuestionScreen(
    questions: List<Question>,
    currentQuestionIndex: Int,
    onAnswerSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPreviousQuestion: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var selectedAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = questions,
                    key = { it.hashCode() }
                ) { question ->
                    QuestionItem(
                        question = question,
                        selectedAnswerIndex = selectedAnswers[questions.indexOf(question)],
                        onAnswerSelected = { index ->
                            selectedAnswers = selectedAnswers + (questions.indexOf(question) to index)
                            onAnswerSelected(index)
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onPreviousQuestion,
                    enabled = currentQuestionIndex > 0
                ) {
                    Text("Previous")
                }

                if (currentQuestionIndex == questions.size - 1) {
                    Button(
                        onClick = onFinish,
                        enabled = selectedAnswers.size == questions.size
                    ) {
                        Text("Finish")
                    }
                } else {
                    Button(
                        onClick = onNextQuestion,
                        enabled = selectedAnswers.containsKey(currentQuestionIndex)
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun QuestionItem(
    question: Question,
    selectedAnswerIndex: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(question.question),
            style = MaterialTheme.typography.titleMedium
        )

        question.image?.let { imageName ->
            val resourceName = imageName.replace(".png", "")
            val imageResource = Res.allDrawableResources[resourceName]
            imageResource?.let { resource ->
                Image(
                    painter = painterResource(resource),
                    contentDescription = "Question image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        question.answers.forEachIndexed { index, answer ->
            val isSelected = selectedAnswerIndex == index
            val isCorrect = selectedAnswerIndex != null && answer == question.trueAnswer
            val isIncorrect = selectedAnswerIndex != null && answer != question.trueAnswer && isSelected

            val backgroundColor = when {
                isCorrect -> Color(0xFF4CAF50)
                isIncorrect -> Color(0xFFE53935)
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }

            val contentColor = when {
                isCorrect || isIncorrect || isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                else -> MaterialTheme.colorScheme.onSurface
            }

            Button(
                onClick = { onAnswerSelected(index) },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAnswerIndex == null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                )
            ) {
                Text(text = stringResource(answer))
            }
        }
    }
} 