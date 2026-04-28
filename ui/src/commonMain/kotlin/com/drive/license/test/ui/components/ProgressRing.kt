package com.drive.license.test.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    trackColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: androidx.compose.ui.graphics.Color? = null,
    strokeWidth: Dp = 16.dp,
    contentDescription: String? = null,
    centerContent: @Composable (() -> Unit)? = null
) {
    val coerced = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(targetValue = coerced)
    val ringColor = progressColor ?: when {
        coerced >= 0.8f -> MaterialTheme.colorScheme.primary
        coerced >= 0.5f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    val defaultDescription = "${(coerced * 100).toInt()}%"
    val semanticsModifier = Modifier.semantics {
        this.contentDescription = contentDescription ?: defaultDescription
    }
    Box(modifier = modifier.size(size).then(semanticsModifier), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                size = Size(this.size.minDimension, this.size.minDimension),
                style = stroke
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                size = Size(this.size.minDimension, this.size.minDimension),
                style = stroke
            )
        }
        if (centerContent != null) centerContent() else {
            Text(text = "${(coerced * 100).toInt()}%", style = MaterialTheme.typography.titleLarge)
        }
    }
}


