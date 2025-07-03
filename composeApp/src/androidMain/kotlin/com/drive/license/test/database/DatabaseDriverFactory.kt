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
                println("[DEBUG_LOG] Prepopulated database copied from assets to: ${databaseFile.absolutePath}")
            } else {
                println("[DEBUG_LOG] Database already exists at: ${databaseFile.absolutePath}")
            }
        } catch (e: Exception) {
            println("[DEBUG_LOG] Error copying prepopulated database: ${e.message}")
        }
    }
}