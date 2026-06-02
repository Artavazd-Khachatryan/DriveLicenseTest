package com.drive.license.test.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = AppColors.primary,
    onPrimary = AppColors.onPrimary,
    primaryContainer = AppColors.primaryContainer,
    onPrimaryContainer = AppColors.onPrimaryContainer,
    inversePrimary = AppColors.inversePrimary,
    secondary = AppColors.secondary,
    onSecondary = AppColors.onSecondary,
    secondaryContainer = AppColors.secondaryContainer,
    onSecondaryContainer = AppColors.onSecondaryContainer,
    tertiary = AppColors.tertiary,
    onTertiary = AppColors.onTertiary,
    tertiaryContainer = AppColors.tertiaryContainer,
    onTertiaryContainer = AppColors.onTertiaryContainer,
    error = AppColors.error,
    onError = AppColors.onError,
    errorContainer = AppColors.errorContainer,
    onErrorContainer = AppColors.onErrorContainer,
    background = AppColors.background,
    onBackground = AppColors.onBackground,
    surface = AppColors.surface,
    onSurface = AppColors.onSurface,
    surfaceVariant = AppColors.surfaceVariant,
    onSurfaceVariant = AppColors.onSurfaceVariant,
    surfaceContainerLowest = AppColors.surfaceContainerLowest,
    surfaceContainerLow = AppColors.surfaceContainerLow,
    surfaceContainer = AppColors.surfaceContainer,
    surfaceContainerHigh = AppColors.surfaceContainerHigh,
    surfaceContainerHighest = AppColors.surfaceContainerHighest,
    outline = AppColors.outline,
    outlineVariant = AppColors.outlineVariant,
    scrim = AppColors.scrim,
    inverseSurface = AppColors.inverseSurface,
    inverseOnSurface = AppColors.inverseOnSurface,
)

private val DarkColors = darkColorScheme(
    primary = AppDarkColors.primary,
    onPrimary = AppDarkColors.onPrimary,
    primaryContainer = AppDarkColors.primaryContainer,
    onPrimaryContainer = AppDarkColors.onPrimaryContainer,
    inversePrimary = AppDarkColors.inversePrimary,
    secondary = AppDarkColors.secondary,
    onSecondary = AppDarkColors.onSecondary,
    secondaryContainer = AppDarkColors.secondaryContainer,
    onSecondaryContainer = AppDarkColors.onSecondaryContainer,
    tertiary = AppDarkColors.tertiary,
    onTertiary = AppDarkColors.onTertiary,
    tertiaryContainer = AppDarkColors.tertiaryContainer,
    onTertiaryContainer = AppDarkColors.onTertiaryContainer,
    error = AppDarkColors.error,
    onError = AppDarkColors.onError,
    errorContainer = AppDarkColors.errorContainer,
    onErrorContainer = AppDarkColors.onErrorContainer,
    background = AppDarkColors.background,
    onBackground = AppDarkColors.onBackground,
    surface = AppDarkColors.surface,
    onSurface = AppDarkColors.onSurface,
    surfaceVariant = AppDarkColors.surfaceVariant,
    onSurfaceVariant = AppDarkColors.onSurfaceVariant,
    surfaceContainerLowest = AppDarkColors.surfaceContainerLowest,
    surfaceContainerLow = AppDarkColors.surfaceContainerLow,
    surfaceContainer = AppDarkColors.surfaceContainer,
    surfaceContainerHigh = AppDarkColors.surfaceContainerHigh,
    surfaceContainerHighest = AppDarkColors.surfaceContainerHighest,
    outline = AppDarkColors.outline,
    outlineVariant = AppDarkColors.outlineVariant,
    scrim = AppDarkColors.scrim,
    inverseSurface = AppDarkColors.inverseSurface,
    inverseOnSurface = AppDarkColors.inverseOnSurface,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography(),
        shapes = AppShapes,
        content = content
    )
}
