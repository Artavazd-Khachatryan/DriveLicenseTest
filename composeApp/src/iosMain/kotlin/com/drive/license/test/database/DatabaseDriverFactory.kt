package com.drive.license.test.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.drive.license.test.LicenseDatabase
import platform.Foundation.*
import kotlinx.cinterop.*

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databaseName = "license_test_questions.db"
        val fileManager = NSFileManager.defaultManager

        // Define the database directory and file paths.
        val appDir = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory, NSUserDomainMask, true
        ).firstOrNull() as? String ?: throw IllegalStateException("Application directory not found")
        val databasesDir = "$appDir/databases"
        val databaseFilePath = "$databasesDir/$databaseName"

        // Ensure the databases directory exists.
        if (!fileManager.fileExistsAtPath(databasesDir)) {
            fileManager.createDirectoryAtPath(databasesDir, withIntermediateDirectories = true, null, null)
        }

        // Copy the database from the app bundle if it doesn't already exist
        if (!fileManager.fileExistsAtPath(databaseFilePath)) {
            // First try to find the database in the main bundle
            var bundleDatabasePath = NSBundle.mainBundle.pathForResource(databaseName, null)
            
            // If not found in main bundle, try compose-resources directory
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

        // Use the standard NativeSqliteDriver with the database name only
        // The driver will automatically find the database in the Application Support directory
        return NativeSqliteDriver(LicenseDatabase.Schema, databaseName)
    }
}