package com.drive.license.test.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun formatCategoryName(raw: String): String =
    raw.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

@Composable
fun accuracyColor(accuracy: Float): Color = when {
    accuracy >= 0.8f -> MaterialTheme.colorScheme.primary
    accuracy >= 0.5f -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.error
}
