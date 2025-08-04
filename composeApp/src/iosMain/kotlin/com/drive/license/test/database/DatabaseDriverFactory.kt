package com.drive.license.test.database

import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.drive.license.test.LicenseDatabase
import platform.Foundation.*

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databaseName = "license_test_questions.db"
        val fileManager = NSFileManager.defaultManager

        val appDir = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory, NSUserDomainMask, true
        ).firstOrNull() as? String ?: throw IllegalStateException("Application directory not found")
        val databasesDir = "$appDir/databases"
        val databaseFilePath = "$databasesDir/$databaseName"

        if (!fileManager.fileExistsAtPath(databasesDir)) {
            fileManager.createDirectoryAtPath(databasesDir, withIntermediateDirectories = true, null, null)
        }

        if (!fileManager.fileExistsAtPath(databaseFilePath)) {
            var bundleDatabasePath = NSBundle.mainBundle.pathForResource(databaseName, null)
            
            if (bundleDatabasePath == null) {
                val composeResourcesPath = NSBundle.mainBundle.pathForResource("compose-resources", null)
                if (composeResourcesPath != null) {
                    bundleDatabasePath = "$composeResourcesPath/$databaseName"
                    if (!fileManager.fileExistsAtPath(bundleDatabasePath)) {
                        bundleDatabasePath = null
                    }
                }
            }
            
            if (bundleDatabasePath == null) {
                throw IllegalStateException("Database file $databaseName not found in bundle or compose-resources")
            }

            try {
                fileManager.copyItemAtPath(bundleDatabasePath, databaseFilePath, null)
                println("Successfully copied database from bundle to $databaseFilePath")
            } catch (e: Exception) {
                throw IllegalStateException("Failed to copy database to $databaseFilePath: ${e.message}")
            }
        } else {
            println("Database already exists at $databaseFilePath")
        }

        return NativeSqliteDriver(LicenseDatabase.Schema, databaseName)
    }
}

@Composable
actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()