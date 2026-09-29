package com.drive.license.test.ui.questioncontext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.Question
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppOutlinedButton
import com.drive.license.test.ui.util.resolveQuestionImage
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.question_context_button
import drivelicensetest.ui.generated.resources.question_context_copied
import drivelicensetest.ui.generated.resources.question_context_copy
import drivelicensetest.ui.generated.resources.question_context_default_follow_up
import drivelicensetest.ui.generated.resources.question_context_hint
import drivelicensetest.ui.generated.resources.question_context_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuestionContextButton(
    question: Question,
    userAnswer: String,
    modifier: Modifier = Modifier,
) {
    var showSheet by remember(question.id) { mutableStateOf(false) }
    AppOutlinedButton(
        text = stringResource(Res.string.question_context_button),
        onClick = { showSheet = true },
        modifier = modifier,
    )
    if (showSheet) {
        QuestionContextSheet(
            question = question,
            userAnswer = userAnswer,
            onDismiss = { showSheet = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionContextSheet(
    question: Question,
    userAnswer: String,
    onDismiss: () -> Unit,
) {
    val actions = rememberQuestionContextActions()
    val defaultFollowUp = stringResource(Res.string.question_context_default_follow_up)
    val copiedMessage = stringResource(Res.string.question_context_copied)
    var copied by remember(question.id) { mutableStateOf(false) }
    val hasImage = resolveQuestionImage(question) != null
    val message = buildQuestionContextMessage(
        question = question.question,
        answers = question.answers,
        userAnswer = userAnswer,
        correctAnswer = question.correctAnswer,
        followUp = defaultFollowUp,
        hasImage = hasImage,
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.question_context_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(Res.string.question_context_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
                    .verticalScroll(rememberScrollState()),
            )
            AppButton(
                text = stringResource(Res.string.question_context_copy),
                onClick = {
                    actions.copy(message)
                    copied = true
                },
                modifier = Modifier.fillMaxWidth(),
            )
            if (copied) {
                Text(
                    text = copiedMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
