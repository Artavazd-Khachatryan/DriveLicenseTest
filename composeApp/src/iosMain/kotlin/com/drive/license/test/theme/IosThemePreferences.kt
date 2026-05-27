package com.drive.license.test.theme

import com.drive.license.test.domain.repository.ThemePreferences
import platform.Foundation.NSUserDefaults

class IosThemePreferences : ThemePreferences {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun loadDarkTheme(): Boolean = defaults.boolForKey(KEY_DARK_THEME)

    override fun saveDarkTheme(dark: Boolean) {
        defaults.setBool(dark, KEY_DARK_THEME)
    }

    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }
}
