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
            questions = uiState.questions,
            selectedAnswers = uiState.selectedAnswers,
            onRetry = {
                viewModel.resetTest()
                showResults = false
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
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
            
            // Debug information (commented out for cleaner UI)
            // Text(
            //     text = "Debug: canMoveToPrevious=${viewModel.canMoveToPrevious()}, canMoveToNext=${viewModel.canMoveToNext()}, isOnLastQuestion=${viewModel.isOnLastQuestion()}",
            //     style = MaterialTheme.typography.bodySmall,
            //     color = MaterialTheme.colorScheme.error
            // )
            
            Spacer(modifier = Modifier.weight(1f))
            
            viewModel.getCurrentQuestion()?.let { question ->
                QuestionItem(
                    question = question,
                    selectedAnswerIndex = viewModel.getSelectedAnswerForCurrentQuestion(),
                    onAnswerSelected = { answerIndex ->
                        viewModel.selectAnswer(answerIndex)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigation buttons - always visible at the bottom
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { 
                            println("[DEBUG] Previous button clicked")
                            viewModel.previousQuestion() 
                        },
                        enabled = viewModel.canMoveToPrevious(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.canMoveToPrevious()) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("Previous")
                    }
                    
                    if (viewModel.isOnLastQuestion()) {
                        Button(
                            onClick = { 
                                println("[DEBUG] Finish button clicked")
                                showResults = true 
                            },
                            enabled = viewModel.canFinish(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.canFinish()) 
                                    Color(0xFF4CAF50) 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Finish")
                        }
                    } else {
                        Button(
                            onClick = { 
                                println("[DEBUG] Next button clicked")
                                viewModel.nextQuestion() 
                            },
                            enabled = viewModel.canMoveToNext(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.canMoveToNext()) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Next")
                        }
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
    var showCorrectAnswer by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = question.question,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Toggle button to show/hide correct answer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show correct answer",
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = showCorrectAnswer,
                onCheckedChange = { showCorrectAnswer = it }
            )
        }
        
        // Show the correct answer if toggle is enabled
        if (showCorrectAnswer) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E8)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Correct Answer:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF2E7D32),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.trueAnswer,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
        
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
        
        // Show the correct answer after user has selected an answer
        if (selectedAnswerIndex != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E8)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Correct Answer:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF2E7D32),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.trueAnswer,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultsScreen(
    score: TestScore,
    questions: List<DatabaseQuestion>,
    selectedAnswers: Map<Int, Int>,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (score.isPassed) "Congratulations!" else "Try Again",
            style = MaterialTheme.typography.headlineMedium,
            color = if (score.isPassed) Color(0xFF4CAF50) else Color(0xFFE53935)
        )
        
        Text(
            text = "Score: ${score.correctAnswers}/${score.totalAnswered}",
            style = MaterialTheme.typography.titleLarge
        )
        
        Text(
            text = "Percentage: ${String.format("%.1f", score.percentage)}%",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Take Test Again")
        }
        
        // Detailed breakdown of questions and answers
        if (questions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Question Review",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
            
            questions.forEachIndexed { index, question ->
                val selectedAnswerIndex = selectedAnswers[index]
                val isCorrect = selectedAnswerIndex != null && 
                               question.answers[selectedAnswerIndex] == question.trueAnswer
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Question ${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        
                        Text(
                            text = question.question,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (selectedAnswerIndex != null) {
                            Text(
                                text = "Your Answer: ${question.answers[selectedAnswerIndex]}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                        
                        Text(
                            text = "Correct Answer: ${question.trueAnswer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
} 
