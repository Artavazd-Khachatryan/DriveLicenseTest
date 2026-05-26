package com.drive.license.test.ui.util

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppLayout {
    val MaxContentWidth: Dp = 720.dp
    val ExpandedMinWidth: Dp = 600.dp
}

@Composable
fun AdaptiveContentContainer(
    modifier: Modifier = Modifier,
    content: @Composable (isExpanded: Boolean, contentModifier: Modifier) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = AppLayout.MaxContentWidth)
    ) {
        val isExpanded = maxWidth >= AppLayout.ExpandedMinWidth
        content(isExpanded, Modifier.fillMaxWidth())
    }
}

fun estimatedTestMinutes(questionCount: Int): Int = when (questionCount) {
    10 -> 3
    20 -> 6
    30 -> 9
    else -> (questionCount * 0.3f).toInt().coerceAtLeast(2)
}
