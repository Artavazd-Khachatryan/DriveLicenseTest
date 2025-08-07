package com.drive.license.test

import androidx.compose.ui.window.ComposeUIViewController
import com.drive.license.test.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App() 
}