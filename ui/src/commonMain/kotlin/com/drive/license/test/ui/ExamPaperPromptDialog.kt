package com.drive.license.test.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.drive.license.test.domain.model.ExamPaper
import com.drive.license.test.ui.components.ExamPaperRadioRows
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.exam_paper_continue
import drivelicensetest.ui.generated.resources.exam_paper_hint
import drivelicensetest.ui.generated.resources.exam_paper_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExamPaperPromptDialog(
    initialPaper: ExamPaper,
    onConfirm: (ExamPaper) -> Unit,
) {
    var selected by remember { mutableStateOf(initialPaper) }
    AlertDialog(
        // A one-time choice: back press or scrim tap must not silently pick the default.
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        title = { Text(stringResource(Res.string.exam_paper_title)) },
        text = {
            Column {
                ExamPaperRadioRows(
                    selected = selected,
                    onSelect = { selected = it },
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.exam_paper_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) {
                Text(stringResource(Res.string.exam_paper_continue))
            }
        },
    )
}
