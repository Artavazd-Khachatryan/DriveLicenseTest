#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

import java.io.File
import kotlinx.coroutines.runBlocking

/**
 * Script to export the populated database for inclusion in builds.
 * Run this script after the database has been populated with all questions.
 */

fun main() = runBlocking {
    println("Exporting populated database...")
    
    // Define the output path for the pre-populated database
    val outputPath = "composeApp/src/commonMain/resources/prepopulated_database.db"
    
    // Create the output directory if it doesn't exist
    val outputFile = File(outputPath)
    outputFile.parentFile?.mkdirs()
    
    // For now, we'll create a placeholder file
    // In a real implementation, you would need to run this from within the app
    // after the database has been populated
    outputFile.writeText("# Pre-populated database file\n# This file should be replaced with the actual database export")
    
    println("Database export script created at: $outputPath")
    println("To actually export the database:")
    println("1. Run the app and ensure all questions are loaded")
    println("2. Use the DatabasePreloader.exportDatabase() method")
    println("3. Copy the resulting database file to this location")
} 