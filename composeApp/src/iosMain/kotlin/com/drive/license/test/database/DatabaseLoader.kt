package com.drive.license.test.database

actual class DatabaseLoader {
    actual fun loadPrepopulatedDatabase(context: Any, assetPath: String, databaseName: String): Boolean {
        // iOS implementation is handled in DatabaseDriverFactory
        return true
    }
    
    actual fun exportDatabase(context: Any, databaseName: String, outputPath: String): Boolean {
        // Not implemented for iOS
        return false
    }
} 