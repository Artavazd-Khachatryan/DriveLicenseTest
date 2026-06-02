package com.drive.license.test.ui.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Subtle scale-down while pressed (~150ms spring). Apply to buttons/cards that own an
 * [InteractionSource] for a tactile, modern press response.
 */
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.97f,
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(),
        label = "press_scale",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
