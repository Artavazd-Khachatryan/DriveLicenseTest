package com.drive.license.test.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
    var widthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val isExpanded = with(density) {
        widthPx > 0 && widthPx.toDp() >= AppLayout.ExpandedMinWidth
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = AppLayout.MaxContentWidth)
            .onSizeChanged { widthPx = it.width },
    ) {
        content(isExpanded, Modifier.fillMaxWidth())
    }
}

fun estimatedTestMinutes(questionCount: Int): Int = when (questionCount) {
    10 -> 3
    20 -> 6
    30 -> 9
    else -> (questionCount * 0.3f).toInt().coerceAtLeast(2)
}
