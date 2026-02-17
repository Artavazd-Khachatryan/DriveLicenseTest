package com.drive.license.test

import android.app.Application
import android.content.Context
import com.drive.license.test.database.appContext

class LicenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationContextProvider.context = applicationContext
        appContext = applicationContext
    }
}

object ApplicationContextProvider {
    lateinit var context: Context
} 