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
        refreshContentIfNeeded(driver)
        return driver
    }

    // When the app update ships newer question content (or a prior broken
    // install left a stub DB with no questions), swap the content tables from
    // the bundled DB while keeping user progress. The bundled DB must exist as
    // a plain file to be ATTACHed, so it is staged into the cache dir.
    private fun refreshContentIfNeeded(driver: SqlDriver) {
        if (!ContentRefresh.isRefreshNeeded(driver)) return
        val staged = java.io.File(appContext.cacheDir, "bundled_$POPULATED_DB_NAME")
        try {
            FileOutputStream(staged).use { it.write(readBundledDatabaseBytes()) }
            ContentRefresh.refresh(driver, staged.absolutePath)
        } finally {
            staged.delete()
        }
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
        // Question.printed_number was added after early builds shipped; DBs
        // created before then lack it and ContentRefresh would fail inserting
        // into it. The ALTER throws if the column already exists — ignore.
        try {
            driver.execute(null, "ALTER TABLE Question ADD COLUMN printed_number INTEGER NOT NULL DEFAULT 0", 0)
        } catch (_: Throwable) {
        }
    }

    // Copy the bundled DB only on first launch. Never overwrite an existing
    // DB here: it holds user progress. Stub or outdated DBs are repaired by
    // refreshContentIfNeeded, which swaps content tables without data loss.
    private fun copyPrepopulatedDatabaseIfNeeded() {
        val databaseFile = appContext.getDatabasePath(POPULATED_DB_NAME)
        if (databaseFile.exists()) return

        databaseFile.parentFile?.mkdirs()
        FileOutputStream(databaseFile).use { it.write(readBundledDatabaseBytes()) }
    }

    private fun readBundledDatabaseBytes(): ByteArray {
        return (javaClass.classLoader?.getResourceAsStream(POPULATED_DB_NAME)
            ?: runCatching { appContext.assets.open(POPULATED_DB_NAME) }.getOrNull()
            ?: error("Pre-populated database '$POPULATED_DB_NAME' not found on classpath or in assets"))
            .use { it.readBytes() }
    }
}

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()