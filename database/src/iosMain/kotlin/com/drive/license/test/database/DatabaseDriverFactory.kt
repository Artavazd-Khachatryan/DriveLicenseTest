package com.drive.license.test.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.drive.license.test.database.Database.Companion.POPULATED_DB_NAME
import platform.Foundation.*

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val fileManager = NSFileManager.defaultManager

        val appDir = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory, NSUserDomainMask, true
        ).firstOrNull() as? String ?: throw IllegalStateException("Application directory not found")
        val databasesDir = "$appDir/databases"
        val databaseFilePath = "$databasesDir/$POPULATED_DB_NAME"

        if (!fileManager.fileExistsAtPath(databasesDir)) {
            fileManager.createDirectoryAtPath(databasesDir, withIntermediateDirectories = true, null, null)
        }

        if (!fileManager.fileExistsAtPath(databaseFilePath)) {
            var bundleDatabasePath = NSBundle.mainBundle.pathForResource(POPULATED_DB_NAME, null)

            if (bundleDatabasePath == null) {
                // Try to find the database in the main bundle root
                val mainBundlePath = NSBundle.mainBundle.bundlePath
                bundleDatabasePath = "$mainBundlePath/$POPULATED_DB_NAME"
                if (!fileManager.fileExistsAtPath(bundleDatabasePath)) {
                    bundleDatabasePath = null
                }
            }

            if (bundleDatabasePath == null) {
                // Try compose-resources as fallback
                val composeResourcesPath = NSBundle.mainBundle.pathForResource("compose-resources", null)
                if (composeResourcesPath != null) {
                    bundleDatabasePath = "$composeResourcesPath/$POPULATED_DB_NAME"
                    if (!fileManager.fileExistsAtPath(bundleDatabasePath)) {
                        bundleDatabasePath = null
                    }
                }
            }

            if (bundleDatabasePath == null) {
                throw IllegalStateException("Database file $POPULATED_DB_NAME not found in bundle, main bundle root, or compose-resources")
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

        return NativeSqliteDriver(LicenseDatabase.Schema, POPULATED_DB_NAME)
    }
}

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()