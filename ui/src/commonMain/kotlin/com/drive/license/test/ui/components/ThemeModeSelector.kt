package com.drive.license.test.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        SegmentedButton(
            selected = !isDarkTheme,
            onClick = { onDarkThemeChange(false) },
            shape = SegmentedButtonDefaults.itemShape(0, 2),
            icon = { Icon(Icons.Default.LightMode, contentDescription = null) },
            label = { Text(stringResource(Res.string.settings_theme_light)) },
        )
        SegmentedButton(
            selected = isDarkTheme,
            onClick = { onDarkThemeChange(true) },
            shape = SegmentedButtonDefaults.itemShape(1, 2),
            icon = { Icon(Icons.Default.DarkMode, contentDescription = null) },
            label = { Text(stringResource(Res.string.settings_theme_dark)) },
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
