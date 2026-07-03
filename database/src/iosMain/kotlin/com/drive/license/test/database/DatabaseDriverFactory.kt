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

        var bundleDatabasePath = NSBundle.mainBundle.pathForResource(POPULATED_DB_NAME, null)

        if (bundleDatabasePath == null) {
            val mainBundlePath = NSBundle.mainBundle.bundlePath
            bundleDatabasePath = "$mainBundlePath/$POPULATED_DB_NAME"
            if (!fileManager.fileExistsAtPath(bundleDatabasePath)) {
                bundleDatabasePath = null
            }
        }

        if (bundleDatabasePath == null) {
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

        // A prior broken install can leave a stub DB created by SqlDelight's Schema.create()
        // that's much smaller than the bundled one. The bundled questions are immutable, so a
        // healthy DB is never smaller than the bundled file. If it is, treat it as a stub.
        val bundledSize = (fileManager.attributesOfItemAtPath(bundleDatabasePath, null)
            ?.get(NSFileSize) as? NSNumber)?.longLongValue ?: 0L
        val existingSize = if (fileManager.fileExistsAtPath(databaseFilePath)) {
            (fileManager.attributesOfItemAtPath(databaseFilePath, null)
                ?.get(NSFileSize) as? NSNumber)?.longLongValue ?: 0L
        } else 0L

        if (existingSize < bundledSize) {
            if (existingSize > 0L) {
                fileManager.removeItemAtPath(databaseFilePath, null)
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

        val driver = NativeSqliteDriver(LicenseDatabase.Schema, POPULATED_DB_NAME)
        ensureMissingTables(driver)
        return driver
    }

    // The bundled license_test_questions.db was generated before UserStreak and
    // BookmarkedQuestion were added to the schema, and SQLDelight only runs
    // Schema.create() on a brand-new DB. Create the missing tables idempotently.
    private fun ensureMissingTables(driver: SqlDriver) {
        driver.execute(null, """
            CREATE TABLE IF NOT EXISTS UserStreak (
                id INTEGER NOT NULL PRIMARY KEY DEFAULT 1,
                current_streak INTEGER NOT NULL DEFAULT 0,
                longest_streak INTEGER NOT NULL DEFAULT 0,
                last_active_day INTEGER
            )
        """.trimIndent(), 0)
        driver.execute(null, """
            CREATE TABLE IF NOT EXISTS BookmarkedQuestion (
                question_id INTEGER NOT NULL PRIMARY KEY,
                bookmarked_at INTEGER NOT NULL,
                FOREIGN KEY (question_id) REFERENCES Question(id)
            )
        """.trimIndent(), 0)
        driver.execute(null, """
            CREATE TABLE IF NOT EXISTS Metadata (
                key TEXT NOT NULL PRIMARY KEY,
                value TEXT NOT NULL
            )
        """.trimIndent(), 0)
    }
}

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()