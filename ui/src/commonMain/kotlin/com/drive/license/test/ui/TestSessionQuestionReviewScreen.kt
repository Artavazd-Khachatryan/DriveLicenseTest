package com.drive.license.test.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.Question
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.domain.util.QuestionTextNormalizer
import com.drive.license.test.ui.components.AnswerButton
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.util.AdaptiveContentContainer
import com.drive.license.test.ui.util.resolveQuestionImage
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.question_image_cd
import drivelicensetest.ui.generated.resources.question_image_unavailable
import drivelicensetest.ui.generated.resources.test_review_not_found
import drivelicensetest.ui.generated.resources.test_review_question_label
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Read-only full question view for a completed test session answer:
 * question text, image (when present), and all options with the user's
 * choice and the correct answer highlighted.
 */
@Composable
fun TestSessionQuestionReviewScreen(
    questionId: Int,
    selectedAnswer: String,
    questionNumber: Int,
    questionRepository: QuestionRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var question by remember { mutableStateOf<Question?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(questionId) {
        question = questionRepository.getQuestionById(questionId)
        isLoading = false
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.test_review_question_label, questionNumber),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        },
    ) { inner ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().then(inner),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            question == null -> {
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
            }
            else -> {
                val q = question!!
                val selectedNormalized = QuestionTextNormalizer.normalize(selectedAnswer)
                val selectedIndex = q.answers.indexOfFirst {
                    QuestionTextNormalizer.normalize(it) == selectedNormalized
                }
                val correctNormalized = QuestionTextNormalizer.normalize(q.correctAnswer)

                AdaptiveContentContainer(
                    modifier = modifier
                        .fillMaxSize()
                        .then(inner)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) { _, contentModifier ->
                    Column(
                        modifier = contentModifier,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        AppCard(
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            val imageResource = resolveQuestionImage(q)
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = q.question,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                                if (q.imageUrl != null || imageResource != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    if (imageResource != null) {
                                        key(q.id) {
                                            Image(
                                                painter = painterResource(imageResource),
                                                contentDescription = stringResource(Res.string.question_image_cd),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(160.dp),
                                                contentScale = ContentScale.Fit,
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.BrokenImage,
                                            contentDescription = stringResource(Res.string.question_image_unavailable),
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        )
                                    }
                                }
                            }
                        }

                        q.answers.forEachIndexed { index, answer ->
                            val answerNormalized = QuestionTextNormalizer.normalize(answer)
                            val isCorrectOption = answerNormalized == correctNormalized
                            val isSelectedOption = index == selectedIndex
                            AnswerButton(
                                answer = answer,
                                index = index,
                                enabled = false,
                                isSelected = isSelectedOption,
                                isCorrect = isCorrectOption,
                                isIncorrect = isSelectedOption && !isCorrectOption,
                                onClick = {},
                            )
                        }
                    }
                }
            }
        }
    }
}
