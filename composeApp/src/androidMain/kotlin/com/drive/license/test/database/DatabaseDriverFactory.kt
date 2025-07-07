package com.drive.license.test.database

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import com.drive.license.test.LicenseDatabase
import java.io.File
import java.io.FileOutputStream

lateinit var appContext: Context

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        copyPrepopulatedDatabaseIfNeeded()
        
        return AndroidSqliteDriver(LicenseDatabase.Schema, appContext, "license_test_questions.db")
    }
    
    private fun copyPrepopulatedDatabaseIfNeeded() {
        try {
            val databaseFile = appContext.getDatabasePath("license_test_questions.db")
            
            if (!databaseFile.exists()) {
                databaseFile.parentFile?.mkdirs()
                
                appContext.assets.open("license_test_questions.db").use { inputStream ->
                    FileOutputStream(databaseFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        } catch (e: Exception) { }
    }
}