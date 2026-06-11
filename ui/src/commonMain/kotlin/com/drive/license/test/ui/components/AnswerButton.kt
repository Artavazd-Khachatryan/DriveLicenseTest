package com.drive.license.test.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.ui.util.answerOptionLabel
import com.drive.license.test.ui.util.pressScale
import com.drive.license.test.ui.util.rememberHaptics

@Composable
fun AnswerButton(
    answer: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isIncorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = -1,
    enabled: Boolean = true,
) {
    val scheme = MaterialTheme.colorScheme
    val targetContainer = when {
        isCorrect -> scheme.secondaryContainer
        isIncorrect -> scheme.errorContainer
        isSelected -> scheme.primaryContainer
        else -> scheme.surfaceContainerLow
    }
    val targetContent = when {
        isCorrect -> scheme.onSecondaryContainer
        isIncorrect -> scheme.onErrorContainer
        isSelected -> scheme.onPrimaryContainer
        else -> scheme.onSurface
    }
    val targetAccent = when {
        isCorrect -> scheme.secondary
        isIncorrect -> scheme.error
        isSelected -> scheme.primary
        else -> scheme.outlineVariant
    }

    val container by animateColorAsState(targetContainer, tween(150), label = "answer_bg")
    val content by animateColorAsState(targetContent, tween(150), label = "answer_fg")
    val accent by animateColorAsState(targetAccent, tween(150), label = "answer_accent")

    val interactionSource = remember { MutableInteractionSource() }
    val haptics = rememberHaptics()

    Surface(
        onClick = {
            if (enabled) {
                haptics.confirm()
                onClick()
            }
        },
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interactionSource),
        shape = MaterialTheme.shapes.medium,
        color = container,
        contentColor = content,
        border = BorderStroke(1.5.dp, accent),
        interactionSource = interactionSource,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnswerBadge(
                index = index,
                isCorrect = isCorrect,
                isIncorrect = isIncorrect,
                accent = accent,
                content = content,
            )
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyLarge,
                color = content,
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(1f),
            )
        }
    }
}

@Composable
private fun AnswerBadge(
    index: Int,
    isCorrect: Boolean,
    isIncorrect: Boolean,
    accent: androidx.compose.ui.graphics.Color,
    content: androidx.compose.ui.graphics.Color,
) {
    Surface(
        shape = CircleShape,
        color = if (isCorrect || isIncorrect) accent else accent.copy(alpha = 0.16f),
        modifier = Modifier.size(32.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            when {
                isCorrect -> Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(20.dp),
                )
                isIncorrect -> Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(20.dp),
                )
                else -> Text(
                    text = answerOptionLabel(index),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = content,
                )
            }
        }
    }
}
