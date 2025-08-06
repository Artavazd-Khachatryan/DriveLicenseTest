package com.drive.license.test.database

expect class DatabaseLoader() {
    fun loadPrepopulatedDatabase(context: Any, assetPath: String, databaseName: String): Boolean
    fun exportDatabase(context: Any, databaseName: String, outputPath: String): Boolean
} 