package com.drive.license.test.domain.repository

interface ThemePreferences {
    fun loadDarkTheme(): Boolean
    fun saveDarkTheme(dark: Boolean)
}
