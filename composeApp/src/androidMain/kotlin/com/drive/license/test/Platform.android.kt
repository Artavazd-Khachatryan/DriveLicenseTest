package com.drive.license.test

import android.os.Build
import androidx.compose.runtime.Composable
import com.drive.license.test.database.DatabaseDriverFactory

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()