package com.drive.license.test.database

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Android-specific database loading functionality.
 */
actual class DatabaseLoader {
    
    /**
     * Loads a pre-populated database from assets to the app's database directory.
     * @param context The Android context
     * @param assetPath The path to the database file in assets
     * @param databaseName The name of the database file
     * @return True if successful, false otherwise
     */
    actual fun loadPrepopulatedDatabase(context: Any, assetPath: String, databaseName: String): Boolean {
        return try {
            val androidContext = context as Context
            val databaseFile = androidContext.getDatabasePath("license_test_questions.db")
            
            // Create the database directory if it doesn't exist
            databaseFile.parentFile?.mkdirs()
            
            // Copy from assets if the database doesn't exist
            if (!databaseFile.exists()) {
                androidContext.assets.open(assetPath).use { inputStream ->
                    FileOutputStream(databaseFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                println("[DEBUG_LOG] Pre-populated database copied from assets to: ${databaseFile.absolutePath}")
                true
            } else {
                println("[DEBUG_LOG] Database already exists at: ${databaseFile.absolutePath}")
                true
            }
        } catch (e: IOException) {
            println("[DEBUG_LOG] Error loading pre-populated database: ${e.message}")
            false
        } catch (e: ClassCastException) {
            println("[DEBUG_LOG] Invalid context type: ${e.message}")
            false
        }
    }
    
    /**
     * Exports the current database to a file for inclusion in builds.
     * @param context The Android context
     * @param databaseName The name of the database file
     * @param outputPath The path where to save the exported database
     * @return True if successful, false otherwise
     */
    actual fun exportDatabase(context: Any, databaseName: String, outputPath: String): Boolean {
        return try {
            val androidContext = context as Context
            val databaseFile = androidContext.getDatabasePath("license_test_questions.db")
            val outputFile = File(outputPath)
            
            if (databaseFile.exists()) {
                outputFile.parentFile?.mkdirs()
                databaseFile.copyTo(outputFile, overwrite = true)
                println("[DEBUG_LOG] Database exported to: ${outputFile.absolutePath}")
                true
            } else {
                println("[DEBUG_LOG] Database file not found: ${databaseFile.absolutePath}")
                false
            }
        } catch (e: IOException) {
            println("[DEBUG_LOG] Error exporting database: ${e.message}")
            false
        } catch (e: ClassCastException) {
            println("[DEBUG_LOG] Invalid context type: ${e.message}")
            false
        }
    }
} 