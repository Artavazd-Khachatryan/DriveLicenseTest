package com.drive.license.test.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.ExamPaper
import com.drive.license.test.ui.theme.AppTheme
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.exam_paper_option_abc
import drivelicensetest.ui.generated.resources.exam_paper_option_dt
import org.jetbrains.compose.resources.stringResource

/** Radio group for choosing the exam paper (A/B/C or D/T). */
@Composable
fun ExamPaperRadioRows(
    selected: ExamPaper,
    onSelect: (ExamPaper) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.selectableGroup()) {
        ExamPaper.entries.forEach { paper ->
            val label = when (paper) {
                ExamPaper.ABC -> stringResource(Res.string.exam_paper_option_abc)
                ExamPaper.DT -> stringResource(Res.string.exam_paper_option_dt)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selected == paper,
                        onClick = { onSelect(paper) },
                        role = Role.RadioButton,
                    )
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selected == paper,
                    onClick = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ExamPaperRadioRowsPreview() {
    AppTheme(darkTheme = false) {
        ExamPaperRadioRows(selected = ExamPaper.ABC, onSelect = {})
    }
}

@Preview
@Composable
private fun ExamPaperRadioRowsDarkPreview() {
    AppTheme(darkTheme = true) {
        ExamPaperRadioRows(selected = ExamPaper.DT, onSelect = {})
    }
}
