package com.drive.license.test.theme

import android.content.Context
import com.drive.license.test.domain.repository.ThemePreferences

class AndroidThemePreferences(context: Context) : ThemePreferences {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun loadDarkTheme(): Boolean = prefs.getBoolean(KEY_DARK_THEME, false)

    override fun saveDarkTheme(dark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, dark).apply()
    }

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_DARK_THEME = "dark_theme"
    }
}
