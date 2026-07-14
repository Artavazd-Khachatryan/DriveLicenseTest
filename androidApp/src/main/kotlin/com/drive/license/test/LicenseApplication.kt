package com.drive.license.test

import android.app.Application
import android.content.Context
import com.drive.license.test.crash.CrashReporting
import com.drive.license.test.database.appContext
import com.drive.license.test.di.initKoin

class LicenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationContextProvider.context = applicationContext
        appContext = applicationContext
        PlatformConfig.init(BuildConfig.ANTHROPIC_API_KEY, BuildConfig.VERSION_NAME)
        initKoin()
        runCatching { CrashReporting.initialize() }
    }
}

object ApplicationContextProvider {
    lateinit var context: Context
} 