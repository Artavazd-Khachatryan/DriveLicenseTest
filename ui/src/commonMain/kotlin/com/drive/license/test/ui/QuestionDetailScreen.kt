package com.drive.license.test.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import com.drive.license.test.domain.model.Question
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppOutlinedButton
import com.drive.license.test.ui.components.AppThemeToggleIcon
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.AnswerButton
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import com.drive.license.test.ui.components.InTestMotivationBanner
import com.drive.license.test.ui.util.InTestMotivationKind
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Timer
import com.drive.license.test.ui.util.AdaptiveContentContainer
import com.drive.license.test.ui.util.resolveQuestionImage
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.question_finish
import drivelicensetest.ui.generated.resources.question_image_cd
import drivelicensetest.ui.generated.resources.question_image_unavailable
import drivelicensetest.ui.generated.resources.question_next
import drivelicensetest.ui.generated.resources.question_number_of_total
import drivelicensetest.ui.generated.resources.question_previous
import drivelicensetest.ui.generated.resources.ai_explain_button
import drivelicensetest.ui.generated.resources.question_bookmark_add
import drivelicensetest.ui.generated.resources.question_bookmark_remove
import drivelicensetest.ui.generated.resources.question_time_remaining
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuestionDetailScreen(
    question: Question,
    questionNumber: Int,
    totalQuestions: Int,
    selectedAnswer: String? = null,
    remainingSeconds: Int? = null,
    isBookmarked: Boolean = false,
    isDarkTheme: Boolean = false,
    onDarkThemeChange: (Boolean) -> Unit = {},
    onBack: () -> Unit,
    onAnswer: (String) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleBookmark: () -> Unit = {},
    onExplain: ((userAnswer: String, correctAnswer: String, isCorrect: Boolean) -> Unit)? = null,
    milestoneKind: InTestMotivationKind? = null,
    milestoneAnsweredCount: Int = 0,
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

    val timerLabel = remainingSeconds?.let {
        val m = it / 60
        val s = it % 60
        "$m:${s.toString().padStart(2, '0')}"
    }
    val timerUrgent = (remainingSeconds ?: Int.MAX_VALUE) < 60
    val timerNormalColor = MaterialTheme.colorScheme.onSurface
    val timerUrgentColor = MaterialTheme.colorScheme.error
    val timerColor by animateColorAsState(
        targetValue = if (timerUrgent) timerUrgentColor else timerNormalColor,
        animationSpec = tween(300),
        label = "timer_color"
    )

    AppScaffold(
        topBarTitle = stringResource(Res.string.question_number_of_total, questionNumber, totalQuestions),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        },
        topBarActions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (timerLabel != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = stringResource(Res.string.question_time_remaining),
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
                AppThemeToggleIcon(
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChange = onDarkThemeChange,
                )
                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isBookmarked)
                            stringResource(Res.string.question_bookmark_remove)
                        else
                            stringResource(Res.string.question_bookmark_add),
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { innerPadding ->
        AdaptiveContentContainer(
            modifier = modifier
                .fillMaxSize()
                .then(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) { isExpanded, contentModifier ->
        Column(
            modifier = contentModifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val questionProgress = if (totalQuestions > 0) {
                questionNumber.toFloat() / totalQuestions
            } else 0f
            LinearProgressIndicator(
                progress = { questionProgress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.small),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )

            AppCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                val imageResource = resolveQuestionImage(question)
                val showImage = imageResource != null
                val imageHeight = if (isExpanded) 140.dp else 160.dp

                if (isExpanded && showImage) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question.question,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        key(question.id) {
                            Image(
                                painter = painterResource(imageResource!!),
                                contentDescription = stringResource(Res.string.question_image_cd),
                                modifier = Modifier
                                    .widthIn(max = 220.dp)
                                    .heightIn(max = imageHeight)
                                    .weight(0.45f),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                } else {
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
                        if (question.imageUrl != null || imageResource != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            if (imageResource != null) {
                                key(question.id) {
                                    Image(
                                        painter = painterResource(imageResource),
                                        contentDescription = stringResource(Res.string.question_image_cd),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(imageHeight),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.BrokenImage,
                                    contentDescription = stringResource(Res.string.question_image_unavailable),
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            }

            question.answers.forEachIndexed { index, answer ->
                AnswerButton(
                    answer = answer,
                    index = index,
                    enabled = !showResult,
                    isSelected = selectedAnswerIndex == index,
                    isCorrect = showResult && answer == question.correctAnswer,
                    isIncorrect = showResult && selectedAnswerIndex == index && answer != question.correctAnswer,
                    onClick = {
                        val isNewSelection = selectedAnswerIndex != index
                        if (!showResult || isNewSelection) {
                            selectedAnswerIndex = index
                            showResult = true
                            onAnswer(answer)
                        }
                    }
                )
            }

            if (AppFeatures.motivationEnabled) {
                InTestMotivationBanner(
                    visible = showResult && milestoneKind != null && milestoneAnsweredCount > 0,
                    kind = milestoneKind ?: InTestMotivationKind.KeepGoing,
                    answeredCount = milestoneAnsweredCount,
                    totalQuestions = totalQuestions,
                )
            }

            if (showResult && onExplain != null) {
                val answeredIndex = selectedAnswerIndex
                if (answeredIndex != null) {
                    val answered = question.answers[answeredIndex]
                    AppButton(
                        text = stringResource(Res.string.ai_explain_button),
                        onClick = {
                            onExplain(answered, question.correctAnswer, answered == question.correctAnswer)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppOutlinedButton(
                    text = stringResource(Res.string.question_previous),
                    onClick = onPrevious,
                    modifier = Modifier.weight(1f),
                    enabled = questionNumber > 1
                )

                Spacer(modifier = Modifier.width(16.dp))

                AppButton(
                    text = if (questionNumber == totalQuestions) stringResource(Res.string.question_finish) else stringResource(Res.string.question_next),
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    enabled = showResult
                )
            }
        }
        }
    }
}
