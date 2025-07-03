package com.drive.license.test.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.models.DatabaseQuestion
import com.drive.license.test.ui.QuestionUiState
import com.drive.license.test.ui.QuestionViewModel
import com.drive.license.test.ui.TestScore
import drivelicensetest.composeapp.generated.resources.Res
import drivelicensetest.composeapp.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun QuestionScreen(
    viewModel: QuestionViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.error != null -> {
                ErrorScreen(error = uiState.error!!)
            }
            uiState.questions.isEmpty() -> {
                EmptyStateScreen()
            }
            else -> {
                QuestionContent(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $error",
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyStateScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No questions available",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuestionContent(
    viewModel: QuestionViewModel,
    uiState: QuestionUiState
) {
    var showResults by remember { mutableStateOf(false) }
    
    if (showResults) {
        ResultsScreen(
            score = viewModel.calculateScore(),
            onRetry = {
                viewModel.resetTest()
                showResults = false
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LinearProgressIndicator(
                progress = if (uiState.questions.isNotEmpty()) {
                    (viewModel.currentQuestionIndex + 1).toFloat() / uiState.questions.size
                } else 0f,
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = "Question ${viewModel.currentQuestionIndex + 1} of ${uiState.questions.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            viewModel.getCurrentQuestion()?.let { question ->
                QuestionItem(
                    question = question,
                    selectedAnswerIndex = viewModel.getSelectedAnswerForCurrentQuestion(),
                    onAnswerSelected = { answerIndex ->
                        viewModel.selectAnswer(answerIndex)
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.previousQuestion() },
                    enabled = viewModel.canMoveToPrevious()
                ) {
                    Text("Previous")
                }
                
                if (viewModel.isOnLastQuestion()) {
                    Button(
                        onClick = { showResults = true },
                        enabled = viewModel.canFinish()
                    ) {
                        Text("Finish")
                    }
                } else {
                    Button(
                        onClick = { viewModel.nextQuestion() },
                        enabled = viewModel.canMoveToNext()
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
    question: DatabaseQuestion,
    selectedAnswerIndex: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = question.question,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
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
            }
        }
        
        question.answers.forEachIndexed { index, answer ->
            val isSelected = selectedAnswerIndex == index
            val trueAnswerText = question.trueAnswer
            val answerText = answer
            val isCorrect = selectedAnswerIndex != null && answerText == trueAnswerText
            val isIncorrect = selectedAnswerIndex != null && answerText != trueAnswerText && isSelected
            
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
                Text(
                    text = answerText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
private fun ResultsScreen(
    score: TestScore,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (score.isPassed) "Congratulations!" else "Try Again",
            style = MaterialTheme.typography.headlineMedium,
            color = if (score.isPassed) Color(0xFF4CAF50) else Color(0xFFE53935)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Score: ${score.correctAnswers}/${score.totalAnswered}",
            style = MaterialTheme.typography.titleLarge
        )
        
        Text(
            text = "Percentage: ${score.percentage}",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Take Test Again")
        }
    }
} 
