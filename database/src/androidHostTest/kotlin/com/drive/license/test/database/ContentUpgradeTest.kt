package com.drive.license.test.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.Query
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import java.sql.Connection
import java.sql.DriverManager
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Simulates an upgrade from the last store release (content_version 2 fixture) to the
 * bundled DB of this build, with user data present.
 */
class ContentUpgradeTest {

    /**
     * Single-connection driver, like the on-device Android driver. The stock
     * JdbcSqliteDriver pools connections, which breaks ATTACH (per connection).
     */
    private class SingleConnectionDriver(path: String) : JdbcDriver() {
        private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$path")
        override fun getConnection(): Connection = connection
        override fun closeConnection(connection: Connection) = Unit
        override fun addListener(vararg queryKeys: String, listener: Query.Listener) = Unit
        override fun removeListener(vararg queryKeys: String, listener: Query.Listener) = Unit
        override fun notifyListeners(vararg queryKeys: String) = Unit
        override fun close() = connection.close()
    }

    private fun JdbcSqliteDriver(url: String): SqlDriver = SingleConnectionDriver(url.removePrefix("jdbc:sqlite:"))

    private val tmpFiles = mutableListOf<File>()

    @AfterTest
    fun cleanUp() {
        tmpFiles.forEach { it.delete() }
    }

    private fun resourceToTemp(resource: String, prefix: String): File {
        val stream = checkNotNull(javaClass.classLoader!!.getResourceAsStream(resource)) {
            "Missing resource $resource"
        }
        val file = File.createTempFile(prefix, ".db")
        tmpFiles += file
        stream.use { input -> file.outputStream().use { input.copyTo(it) } }
        return file
    }

    private fun bundledCopy() = resourceToTemp("license_test_questions.db", "bundled")

    private fun SqlDriver.exec(sql: String) {
        execute(null, sql, 0)
    }

    private fun SqlDriver.long(sql: String): Long = executeQuery(
        null, sql,
        { c -> QueryResult.Value(if (c.next().value) c.getLong(0) else null) }, 0,
    ).value ?: 0L

    private fun SqlDriver.string(sql: String): String? = executeQuery(
        null, sql,
        { c -> QueryResult.Value(if (c.next().value) c.getString(0) else null) }, 0,
    ).value

    private fun SqlDriver.longs(sql: String): List<Long> = executeQuery(
        null, sql,
        { c ->
            val out = mutableListOf<Long>()
            while (c.next().value) out += c.getLong(0)!!
            QueryResult.Value(out)
        }, 0,
    ).value

    private fun SqlDriver.groupCounts(): Map<String, Long> = executeQuery(
        null, "SELECT exam_group, COUNT(*) FROM Question GROUP BY exam_group",
        { c ->
            val out = mutableMapOf<String, Long>()
            while (c.next().value) out[c.getString(0)!!] = c.getLong(1)!!
            QueryResult.Value(out)
        }, 0,
    ).value

    @Test
    fun upgradeFromReleaseV1_1_0_14_keepsProgressAndSwapsContent() {
        val installed = resourceToTemp("fixtures/release_v1_1_0_14.db", "installed")
        val bundled = bundledCopy()

        // Expected values come from the bundled DB itself.
        val bundledDriver = JdbcSqliteDriver("jdbc:sqlite:${bundled.absolutePath}")
        val expectedQuestions = bundledDriver.long("SELECT COUNT(*) FROM Question")
        val expectedGroups = bundledDriver.groupCounts()
        val expectedSigns = bundledDriver.long("SELECT COUNT(*) FROM TrafficSign")
        val bundledIds = bundledDriver.longs("SELECT id FROM Question").toSet()
        bundledDriver.close()

        val keptIds = listOf(1L, 2L, 3L)
        val removedIds = listOf(30L, 35L, 44L)
        assertTrue(keptIds.all { it in bundledIds }, "kept ids must exist in bundled DB")
        assertTrue(removedIds.none { it in bundledIds }, "removed ids must be absent from bundled DB")

        val driver = JdbcSqliteDriver("jdbc:sqlite:${installed.absolutePath}")
        try {
            driver.exec("INSERT INTO TestSession (id, start_time, end_time, total_questions, correct_answers, is_completed) VALUES ('s1', 1000, 2000, 6, 3, 1)")
            (keptIds + removedIds).forEach { id ->
                driver.exec("INSERT INTO UserQuestionProgress (question_id, times_answered, times_correct, times_incorrect, is_learned, last_answered_at) VALUES ($id, 2, 1, 1, 0, 1500)")
                driver.exec("INSERT INTO QuestionAttempt (session_id, question_id, selected_answer, is_correct, time_spent, attempt_time) VALUES ('s1', $id, 'a', 1, 1000, 1500)")
            }
            driver.exec("INSERT INTO UserStatistics (id, total_questions_attempted, total_correct_answers, total_incorrect_answers, total_test_sessions, total_passed_sessions, last_updated) VALUES (1, 6, 3, 3, 1, 0, 2000)")

            SchemaEnsure.apply(driver)
            driver.exec("INSERT INTO BookmarkedQuestion (question_id, bookmarked_at) VALUES (1, 1600)")
            driver.exec("INSERT INTO BookmarkedQuestion (question_id, bookmarked_at) VALUES (30, 1600)")

            assertTrue(ContentRefresh.isRefreshNeeded(driver), "fixture is an older content version")

            ContentRefresh.refresh(driver, bundled.absolutePath)

            assertEquals(ContentRefresh.CONTENT_VERSION.toString(), driver.string("SELECT value FROM Metadata WHERE key='content_version'"))
            assertEquals(expectedQuestions, driver.long("SELECT COUNT(*) FROM Question"))
            assertEquals(expectedGroups, driver.groupCounts())
            assertEquals(expectedSigns, driver.long("SELECT COUNT(*) FROM TrafficSign"))

            assertEquals(keptIds.map { it }, driver.longs("SELECT question_id FROM UserQuestionProgress ORDER BY question_id"))
            assertEquals(keptIds.map { it }, driver.longs("SELECT question_id FROM QuestionAttempt ORDER BY question_id"))
            assertEquals(listOf(1L), driver.longs("SELECT question_id FROM BookmarkedQuestion"))

            assertEquals(1L, driver.long("SELECT COUNT(*) FROM UserStatistics"))
            assertEquals(6L, driver.long("SELECT total_questions_attempted FROM UserStatistics WHERE id = 1"))
            assertEquals(3L, driver.long("SELECT total_correct_answers FROM UserStatistics WHERE id = 1"))
            assertEquals(1L, driver.long("SELECT COUNT(*) FROM TestSession WHERE id = 's1'"))

            assertEquals("ok", driver.string("PRAGMA integrity_check"))

            assertFalse(ContentRefresh.isRefreshNeeded(driver), "refresh must be idempotent")
        } finally {
            driver.close()
        }
    }

    @Test
    fun bundledDbIsStampedAndHasEmptyUserTables() {
        val bundled = bundledCopy()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${bundled.absolutePath}")
        try {
            assertEquals(
                ContentRefresh.CONTENT_VERSION.toString(),
                driver.string("SELECT value FROM Metadata WHERE key='content_version'"),
                "ContentRefresh.CONTENT_VERSION must equal the stamp in the bundled DB",
            )
            listOf("UserQuestionProgress", "TestSession", "QuestionAttempt", "UserStatistics").forEach { t ->
                assertEquals(0L, driver.long("SELECT COUNT(*) FROM $t"), "$t must be empty in bundled DB")
            }
        } finally {
            driver.close()
        }
    }
}
