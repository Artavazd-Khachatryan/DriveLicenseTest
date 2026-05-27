package com.drive.license.test.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.settings_theme_dark
import drivelicensetest.ui.generated.resources.settings_theme_light
import drivelicensetest.ui.generated.resources.settings_theme_toggle_dark_cd
import drivelicensetest.ui.generated.resources.settings_theme_toggle_light_cd
import org.jetbrains.compose.resources.stringResource

@Composable
fun ThemeModeSelector(
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = !isDarkTheme,
            onClick = { onDarkThemeChange(false) },
            label = { Text(stringResource(Res.string.settings_theme_light)) },
            leadingIcon = if (!isDarkTheme) {
                { Icon(Icons.Default.LightMode, contentDescription = null) }
            } else null,
        )
        FilterChip(
            selected = isDarkTheme,
            onClick = { onDarkThemeChange(true) },
            label = { Text(stringResource(Res.string.settings_theme_dark)) },
            leadingIcon = if (isDarkTheme) {
                { Icon(Icons.Default.DarkMode, contentDescription = null) }
            } else null,
        )
    }
}

@Composable
fun AppThemeToggleIcon(
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { onDarkThemeChange(!isDarkTheme) },
        modifier = modifier,
    ) {
        Icon(
            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = stringResource(
                if (isDarkTheme) Res.string.settings_theme_toggle_light_cd
                else Res.string.settings_theme_toggle_dark_cd
            ),
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}
