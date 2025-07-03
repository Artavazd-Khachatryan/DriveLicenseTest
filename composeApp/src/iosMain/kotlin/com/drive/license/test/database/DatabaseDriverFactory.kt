package com.drive.license.test.database

import app.cash.sqldelight.db.SqlDriver
import com.drive.license.test.LicenseDatabase
import app.cash.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(LicenseDatabase.Schema, "license_test_questions.db")
    }
}