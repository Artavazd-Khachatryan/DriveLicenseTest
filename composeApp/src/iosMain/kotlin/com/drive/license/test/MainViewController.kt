package com.drive.license.test

import androidx.compose.ui.window.ComposeUIViewController
import com.drive.license.test.crash.CrashReporting
import com.drive.license.test.di.initKoin
import platform.UIKit.UIRectEdgeAll

fun MainViewController() = ComposeUIViewController {
    runCatching { CrashReporting.initialize() }
    initKoin()
    App()
}.apply {
    edgesForExtendedLayout = UIRectEdgeAll
    extendedLayoutIncludesOpaqueBars = true
}