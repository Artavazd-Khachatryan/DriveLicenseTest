package com.drive.license.test.database

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import com.drive.license.test.database.Database.Companion.POPULATED_DB_NAME
import java.io.FileOutputStream

lateinit var appContext: Context

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        copyPrepopulatedDatabaseIfNeeded()

        val driver = AndroidSqliteDriver(LicenseDatabase.Schema, appContext, POPULATED_DB_NAME)
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
    }
    
    private fun copyPrepopulatedDatabaseIfNeeded() {
        val databaseFile = appContext.getDatabasePath(POPULATED_DB_NAME)
        if (databaseFile.exists()) return

        databaseFile.parentFile?.mkdirs()

        val inputStream = javaClass.classLoader?.getResourceAsStream(POPULATED_DB_NAME)
            ?: runCatching { appContext.assets.open(POPULATED_DB_NAME) }.getOrNull()
            ?: error("Pre-populated database '$POPULATED_DB_NAME' not found on classpath or in assets")

        inputStream.use { input ->
            FileOutputStream(databaseFile).use { output -> input.copyTo(output) }
        }
    }
}

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()