package com.drive.license.test.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver

/**
 * Idempotent patches for on-device databases that were copied from an older
 * bundled file. SQLDelight only runs Schema.create() on a brand-new empty DB.
 *
 * Must run **before** [ContentRefresh]: the installed Question table needs
 * [EXAM_GROUP_COLUMN] so the bundled INSERT can copy that column.
 */
object SchemaEnsure {

    const val EXAM_GROUP_COLUMN = "exam_group"
    const val EXAM_GROUP_GENERAL = "GENERAL"

    fun apply(driver: SqlDriver) {
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
        addExamGroupColumnIfMissing(driver)
        driver.execute(null, """
            CREATE TABLE IF NOT EXISTS TrafficSign (
                id INTEGER NOT NULL PRIMARY KEY,
                code TEXT NOT NULL UNIQUE,
                category TEXT NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                image TEXT
            )
        """.trimIndent(), 0)
    }

    private fun addExamGroupColumnIfMissing(driver: SqlDriver) {
        if (questionHasExamGroup(driver)) return
        driver.execute(
            null,
            "ALTER TABLE Question ADD COLUMN $EXAM_GROUP_COLUMN TEXT NOT NULL DEFAULT '$EXAM_GROUP_GENERAL'",
            0,
        )
    }

    private fun questionHasExamGroup(driver: SqlDriver): Boolean {
        return driver.executeQuery(
            identifier = null,
            sql = "PRAGMA table_info(Question)",
            mapper = { cursor ->
                var found = false
                while (cursor.next().value) {
                    if (cursor.getString(1) == EXAM_GROUP_COLUMN) {
                        found = true
                        break
                    }
                }
                QueryResult.Value(found)
            },
            parameters = 0,
        ).value
    }
}
