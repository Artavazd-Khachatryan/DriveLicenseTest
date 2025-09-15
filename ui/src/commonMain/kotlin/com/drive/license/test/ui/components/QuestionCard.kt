package com.drive.license.test.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.Question

@Composable
fun QuestionCard(
    question: Question,
    questionNumber: Int,
    totalQuestions: Int,
    onQuestionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onQuestionClick() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question $questionNumber of $totalQuestions",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                QuestionStatusChip(
                    status = QuestionStatus.UNANSWERED,
                    modifier = Modifier
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${question.answers.size} answers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuestionStatusChip(
    status: QuestionStatus,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        QuestionStatus.UNANSWERED -> "Unanswered" to MaterialTheme.colorScheme.onSurfaceVariant
        QuestionStatus.CORRECT -> "Correct" to MaterialTheme.colorScheme.primary
        QuestionStatus.INCORRECT -> "Incorrect" to MaterialTheme.colorScheme.error
    }
    
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
    )
}

enum class QuestionStatus {
    UNANSWERED,
    CORRECT,
    INCORRECT
}
