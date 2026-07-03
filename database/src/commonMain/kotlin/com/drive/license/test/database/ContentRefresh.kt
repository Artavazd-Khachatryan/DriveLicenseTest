package com.drive.license.test.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver

/**
 * Refreshes question content on an installed database from the bundled one
 * without touching user progress.
 *
 * The bundled DB carries a `content_version` stamp in its Metadata table
 * (written by scripts/set_content_version.py). [CONTENT_VERSION] must be kept
 * equal to that stamp. When the installed DB reports an older version, the
 * content tables are swapped wholesale from the bundled file and rows in user
 * tables pointing at removed questions are deleted.
 */
object ContentRefresh {

    /** Version of the question content bundled with this build. Must match the
     * `content_version` stamped into license_test_questions.db. */
    const val CONTENT_VERSION = 1L

    private const val VERSION_KEY = "content_version"

    fun isRefreshNeeded(driver: SqlDriver): Boolean = installedVersion(driver) < CONTENT_VERSION

    /**
     * Replaces Book/QuestionCategory/Question/QuestionCategoryJunction with the
     * contents of the bundled database at [bundledDbPath], deletes orphaned
     * progress/bookmark/attempt rows, and stamps [CONTENT_VERSION].
     * All content changes happen in a single transaction.
     */
    fun refresh(driver: SqlDriver, bundledDbPath: String) {
        driver.execute(null, "ATTACH DATABASE ? AS bundled", 1) {
            bindString(0, bundledDbPath)
        }
        try {
            driver.execute(null, "BEGIN IMMEDIATE", 0)
            try {
                contentSwapStatements.forEach { driver.execute(null, it, 0) }
                driver.execute(null, "INSERT OR REPLACE INTO Metadata (key, value) VALUES ('$VERSION_KEY', '$CONTENT_VERSION')", 0)
                driver.execute(null, "COMMIT", 0)
            } catch (t: Throwable) {
                driver.execute(null, "ROLLBACK", 0)
                throw t
            }
        } finally {
            driver.execute(null, "DETACH DATABASE bundled", 0)
        }
    }

    private val contentSwapStatements = listOf(
        "DELETE FROM QuestionCategoryJunction",
        "DELETE FROM Question",
        "DELETE FROM Book",
        "DELETE FROM QuestionCategory",
        "INSERT INTO Book (id, name) SELECT id, name FROM bundled.Book",
        "INSERT INTO QuestionCategory (id, name) SELECT id, name FROM bundled.QuestionCategory",
        "INSERT INTO Question (id, question, image, answers, true_answer, book_id, printed_number) " +
            "SELECT id, question, image, answers, true_answer, book_id, printed_number FROM bundled.Question",
        "INSERT INTO QuestionCategoryJunction (question_id, category_id) " +
            "SELECT question_id, category_id FROM bundled.QuestionCategoryJunction",
        // Progress on questions that no longer exist must not linger: it would
        // skew statistics joins and break bookmark/review lookups.
        "DELETE FROM UserQuestionProgress WHERE question_id NOT IN (SELECT id FROM Question)",
        "DELETE FROM QuestionAttempt WHERE question_id NOT IN (SELECT id FROM Question)",
        "DELETE FROM BookmarkedQuestion WHERE question_id NOT IN (SELECT id FROM Question)",
    )

    private fun installedVersion(driver: SqlDriver): Long {
        return driver.executeQuery(
            identifier = null,
            sql = "SELECT value FROM Metadata WHERE key = '$VERSION_KEY'",
            mapper = { cursor ->
                QueryResult.Value(if (cursor.next().value) cursor.getString(0) else null)
            },
            parameters = 0,
        ).value?.toLongOrNull() ?: 0L
    }
}
