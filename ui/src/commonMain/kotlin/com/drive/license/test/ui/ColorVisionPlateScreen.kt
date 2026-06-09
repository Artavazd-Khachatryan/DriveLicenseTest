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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.ColorVisionPlate
import com.drive.license.test.ui.components.AnswerButton
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppOutlinedButton
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.util.AdaptiveContentContainer
import com.drive.license.test.ui.util.resolveDrawableResource
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.color_vision_image_cd
import drivelicensetest.ui.generated.resources.color_vision_image_unavailable
import drivelicensetest.ui.generated.resources.color_vision_plate_prompt
import drivelicensetest.ui.generated.resources.color_vision_plate_number_of_total
import drivelicensetest.ui.generated.resources.question_finish
import drivelicensetest.ui.generated.resources.question_next
import drivelicensetest.ui.generated.resources.question_previous
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ColorVisionPlateScreen(
    plate: ColorVisionPlate,
    plateNumber: Int,
    totalPlates: Int,
    selectedAnswer: String? = null,
    onBack: () -> Unit,
    onAnswer: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showResult by remember(plate.id) { mutableStateOf(selectedAnswer != null) }
    var selectedAnswerIndex by remember(plate.id) {
        mutableStateOf(plate.options.indexOf(selectedAnswer).takeIf { it >= 0 })
    }

    AppScaffold(
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        },
        topBarTitle = stringResource(
            Res.string.color_vision_plate_number_of_total,
            plateNumber,
            totalPlates,
        ),
    ) { inner ->
        AdaptiveContentContainer(
            modifier = modifier
                .fillMaxSize()
                .then(inner)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) { isExpanded, contentModifier ->
            Column(
                modifier = contentModifier,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val plateProgress = if (totalPlates > 0) {
                    plateNumber.toFloat() / totalPlates
                } else 0f
                LinearProgressIndicator(
                    progress = { plateProgress.coerceIn(0f, 1f) },
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    val imageResource = resolveDrawableResource(plate.imageName)
                    val imageHeight = if (isExpanded) 220.dp else 260.dp

                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(Res.string.color_vision_plate_prompt),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (imageResource != null) {
                            Image(
                                painter = painterResource(imageResource),
                                contentDescription = stringResource(Res.string.color_vision_image_cd),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = imageHeight),
                                contentScale = ContentScale.Fit,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = stringResource(Res.string.color_vision_image_unavailable),
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            )
                        }
                    }
                }

                plate.options.forEachIndexed { index, answer ->
                    AnswerButton(
                        answer = answer,
                        index = index,
                        enabled = !showResult,
                        isSelected = selectedAnswerIndex == index,
                        isCorrect = showResult && answer == plate.correctAnswer,
                        isIncorrect = showResult &&
                            selectedAnswerIndex == index &&
                            answer != plate.correctAnswer,
                        onClick = {
                            if (!showResult) {
                                selectedAnswerIndex = index
                                showResult = true
                                onAnswer(answer)
                            }
                        },
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AppOutlinedButton(
                        text = stringResource(Res.string.question_previous),
                        onClick = onPrevious,
                        modifier = Modifier.weight(1f),
                        enabled = plateNumber > 1,
                    )
                    AppButton(
                        text = if (plateNumber >= totalPlates) {
                            stringResource(Res.string.question_finish)
                        } else {
                            stringResource(Res.string.question_next)
                        },
                        onClick = onNext,
                        modifier = Modifier.weight(1f),
                        enabled = showResult,
                    )
                }
            }
        }
    }
}
