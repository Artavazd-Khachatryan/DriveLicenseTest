package com.drive.license.test

import platform.UIKit.UIDevice
import androidx.compose.runtime.Composable
import com.drive.license.test.database.DatabaseDriverFactory

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()