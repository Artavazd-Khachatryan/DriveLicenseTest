package com.drive.license.test.ui

sealed class Screen {
    object Home : Screen()
    object Stats : Screen()
    object Practice : Screen()
    object CategoryPicker : Screen()
    object Question : Screen()
    object Results : Screen()
    object Mistakes : Screen()
    object Bookmarks : Screen()

    val isTopLevel: Boolean
        get() = this is Home || this is Stats || this is Practice
}
