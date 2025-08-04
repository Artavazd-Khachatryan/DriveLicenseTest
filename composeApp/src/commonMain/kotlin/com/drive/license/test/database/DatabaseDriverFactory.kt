package com.drive.license.test.database

import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory() {
    fun createDriver(): SqlDriver
}

@Composable
expect fun getDatabaseDriverFactory(): DatabaseDriverFactory