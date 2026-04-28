package com.drive.license.test.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import com.drive.license.test.domain.model.Question
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.AnswerButton
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Timer
import com.drive.license.test.ui.util.resolveDrawableResource

@Composable
fun QuestionDetailScreen(
    question: Question,
    questionNumber: Int,
    totalQuestions: Int,
    selectedAnswer: String? = null,
    remainingSeconds: Int? = null,
    onBack: () -> Unit,
    onAnswer: (String) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExitDialog by remember { mutableStateOf(false) }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit test?") },
            text = { Text("Your progress will be lost. Are you sure you want to leave?") },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; onBack() }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Continue")
                }
            }
        )
    }

    var selectedAnswerIndex by remember(question.id) {
        mutableStateOf(
            if (selectedAnswer != null) {
                question.answers.indexOf(selectedAnswer).takeIf { it >= 0 }
            } else null
        )
    }
    var showResult by remember(question.id) { mutableStateOf(selectedAnswer != null) }

    val timerLabel = remainingSeconds?.let {
        val m = it / 60
        val s = it % 60
        "%d:%02d".format(m, s)
    }
    val timerUrgent = (remainingSeconds ?: Int.MAX_VALUE) < 60
    val timerColor by animateColorAsState(
        targetValue = if (timerUrgent) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Unspecified,
        animationSpec = tween(300),
        label = "timer_color"
    )

    AppScaffold(
        topBarTitle = "Question $questionNumber of $totalQuestions",
        navigationIcon = {
            IconButton(onClick = { showExitDialog = true }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        topBarActions = if (timerLabel != null) {
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Time remaining",
                        tint = timerColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = timerLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = timerColor
                    )
                }
            }
        } else null
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
                    
                    question.imageUrl?.let { imageName ->
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        AppCard(
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val imageResource = resolveDrawableResource(imageName)
                                if (imageResource != null) {
                                    Image(
                                        painter = painterResource(imageResource),
                                        contentDescription = "Question image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = "Image not available",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .padding(vertical = 16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
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
