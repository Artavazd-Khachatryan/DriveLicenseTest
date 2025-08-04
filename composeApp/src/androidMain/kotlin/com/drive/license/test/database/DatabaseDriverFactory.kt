package com.drive.license.test.database

import android.content.Context
import androidx.compose.runtime.Composable
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import com.drive.license.test.LicenseDatabase
import com.drive.license.test.database.Database.Companion.POPULATED_DB_NAME
import java.io.FileOutputStream

lateinit var appContext: Context

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        copyPrepopulatedDatabaseIfNeeded()
        
        return AndroidSqliteDriver(LicenseDatabase.Schema, appContext, POPULATED_DB_NAME)
    }
    
    private fun copyPrepopulatedDatabaseIfNeeded() {
        try {
            val databaseFile = appContext.getDatabasePath(POPULATED_DB_NAME)
            
            if (!databaseFile.exists()) {
                databaseFile.parentFile?.mkdirs()
                
                appContext.assets.open(POPULATED_DB_NAME).use { inputStream ->
                    FileOutputStream(databaseFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        } catch (e: Exception) { }
    }
}

@Composable
actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()