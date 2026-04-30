package com.drive.license.test.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AnswerButton(
    answer: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isIncorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetBackground = when {
        isCorrect -> MaterialTheme.colorScheme.primary
        isIncorrect -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val targetContent = when {
        isCorrect -> MaterialTheme.colorScheme.onPrimary
        isIncorrect -> MaterialTheme.colorScheme.onError
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetBackground,
        animationSpec = tween(150),
        label = "answer_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = targetContent,
        animationSpec = tween(150),
        label = "answer_content"
    )

    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
