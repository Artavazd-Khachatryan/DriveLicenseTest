package com.drive.license.test.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Full Material 3 palette for the app. Every role used in the UI is defined here so nothing
 * falls back to Material's default purple. Colors are kept in light/dark pairs.
 */
object AppColors {
    // Brand — blue
    val primary = Color(0xFF2563EB)
    val onPrimary = Color(0xFFFFFFFF)
    val primaryContainer = Color(0xFFD9E6FF)
    val onPrimaryContainer = Color(0xFF001A41)

    // Brand — green (success / progress)
    val secondary = Color(0xFF16A34A)
    val onSecondary = Color(0xFFFFFFFF)
    val secondaryContainer = Color(0xFFC9F7D5)
    val onSecondaryContainer = Color(0xFF002110)

    // Accent — amber/violet for AI & highlights
    val tertiary = Color(0xFF7C3AED)
    val onTertiary = Color(0xFFFFFFFF)
    val tertiaryContainer = Color(0xFFEADDFF)
    val onTertiaryContainer = Color(0xFF21005D)

    val error = Color(0xFFDC2626)
    val onError = Color(0xFFFFFFFF)
    val errorContainer = Color(0xFFFFDAD6)
    val onErrorContainer = Color(0xFF410002)

    val background = Color(0xFFF6F8FC)
    val onBackground = Color(0xFF0F172A)
    val surface = Color(0xFFFFFFFF)
    val onSurface = Color(0xFF0F172A)
    val surfaceVariant = Color(0xFFE7ECF3)
    val onSurfaceVariant = Color(0xFF475569)

    // Tonal surface containers (M3 elevation system)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF1F5F9)
    val surfaceContainer = Color(0xFFEDF1F7)
    val surfaceContainerHigh = Color(0xFFE7ECF3)
    val surfaceContainerHighest = Color(0xFFE1E7EF)

    val outline = Color(0xFF94A3B8)
    val outlineVariant = Color(0xFFCBD5E1)
    val scrim = Color(0xFF000000)
    val inverseSurface = Color(0xFF1E293B)
    val inverseOnSurface = Color(0xFFF1F5F9)
    val inversePrimary = Color(0xFF9DC0FF)
}

object AppDarkColors {
    val primary = Color(0xFF9DC0FF)
    val onPrimary = Color(0xFF002F66)
    val primaryContainer = Color(0xFF1D4ED8)
    val onPrimaryContainer = Color(0xFFD9E6FF)

    val secondary = Color(0xFF86EFAC)
    val onSecondary = Color(0xFF003919)
    val secondaryContainer = Color(0xFF15803D)
    val onSecondaryContainer = Color(0xFFC9F7D5)

    val tertiary = Color(0xFFD0BCFF)
    val onTertiary = Color(0xFF381E72)
    val tertiaryContainer = Color(0xFF6D28D9)
    val onTertiaryContainer = Color(0xFFEADDFF)

    val error = Color(0xFFFFB4AB)
    val onError = Color(0xFF690005)
    val errorContainer = Color(0xFF93000A)
    val onErrorContainer = Color(0xFFFFDAD6)

    val background = Color(0xFF0B1120)
    val onBackground = Color(0xFFE2E8F0)
    val surface = Color(0xFF0F172A)
    val onSurface = Color(0xFFE2E8F0)
    val surfaceVariant = Color(0xFF334155)
    val onSurfaceVariant = Color(0xFFCBD5E1)

    val surfaceContainerLowest = Color(0xFF070C16)
    val surfaceContainerLow = Color(0xFF111A2E)
    val surfaceContainer = Color(0xFF16203A)
    val surfaceContainerHigh = Color(0xFF1E293B)
    val surfaceContainerHighest = Color(0xFF273349)

    val outline = Color(0xFF64748B)
    val outlineVariant = Color(0xFF334155)
    val scrim = Color(0xFF000000)
    val inverseSurface = Color(0xFFE2E8F0)
    val inverseOnSurface = Color(0xFF1E293B)
    val inversePrimary = Color(0xFF2563EB)
}
