package com.drive.license.test.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = AppColors.primary,
    onPrimary = AppColors.onPrimary,
    primaryContainer = AppColors.primaryContainer,
    onPrimaryContainer = AppColors.onPrimaryContainer,
    secondary = AppColors.secondary,
    onSecondary = AppColors.onSecondary,
    secondaryContainer = AppColors.secondaryContainer,
    onSecondaryContainer = AppColors.onSecondaryContainer,
    background = AppColors.background,
    onBackground = AppColors.onBackground,
    surface = AppColors.surface,
    onSurface = AppColors.onSurface,
    surfaceVariant = AppColors.surfaceVariant,
    onSurfaceVariant = AppColors.onSurfaceVariant,
    error = AppColors.error,
    onError = AppColors.onError
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFD6E6FF),
    secondary = Color(0xFF86EFAC),
    onSecondary = Color(0xFF06210F),
    secondaryContainer = Color(0xFF14532D),
    onSecondaryContainer = Color(0xFFCFF7D8),
    background = Color(0xFF0B1120),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = Color(0xFFF87171),
    onError = Color(0xFF0B1220)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}


