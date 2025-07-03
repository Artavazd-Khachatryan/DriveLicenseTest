#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

import java.io.File

/**
 * Simple test script to verify our database system works.
 */

fun main() {
    println("=== Database System Test ===")
    
    // Test 1: Check if all required files exist
    testFileExistence()
    
    // Test 2: Check if build succeeds
    testBuildSuccess()
    
    // Test 3: Check database structure
    testDatabaseStructure()
    
    println("=== Test completed! ===")
}

fun testFileExistence() {
    println("\n[TEST] Checking file existence...")
    
    val requiredFiles = listOf(
        "composeApp/src/commonMain/kotlin/com/drive/license/test/database/Database.kt",
        "composeApp/src/commonMain/kotlin/com/drive/license/test/database/DatabaseInitializer.kt",
        "composeApp/src/commonMain/kotlin/com/drive/license/test/database/DatabaseLoader.kt",
        "composeApp/src/androidMain/kotlin/com/drive/license/test/database/DatabaseLoader.android.kt",
        "composeApp/src/commonMain/kotlin/com/drive/license/test/database/DatabaseExportUtil.kt",
        "composeApp/src/commonMain/kotlin/com/drive/license/test/database/DatabaseTest.kt",
        "composeApp/src/commonMain/resources/prepopulated_database.db",
        "composeApp/build.gradle.kts"
    )
    
    var allFilesExist = true
    requiredFiles.forEach { filePath ->
        val file = File(filePath)
        val exists = file.exists()
        println("  ${if (exists) "✓" else "✗"} $filePath")
        if (!exists) allFilesExist = false
    }
    
    if (allFilesExist) {
        println("  ✓ All required files exist")
    } else {
        println("  ✗ Some required files are missing")
    }
}

fun testBuildSuccess() {
    println("\n[TEST] Checking build success...")
    
    try {
        val process = ProcessBuilder("./gradlew", ":composeApp:assembleDebug")
            .directory(File("."))
            .redirectErrorStream(true)
            .start()
        
        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        
        if (exitCode == 0) {
            println("  ✓ Build succeeded")
        } else {
            println("  ✗ Build failed with exit code: $exitCode")
            println("  Output: $output")
        }
    } catch (e: Exception) {
        println("  ✗ Build test failed: ${e.message}")
    }
}

fun testDatabaseStructure() {
    println("\n[TEST] Checking database structure...")
    
    // Check if SQLDelight files exist
    val sqldelightFiles = listOf(
        "composeApp/src/commonMain/sqldelight/com/drive/license/test/database/Question.sq",
        "composeApp/src/commonMain/sqldelight/com/drive/license/test/database/Book.sq",
        "composeApp/src/commonMain/sqldelight/com/drive/license/test/database/Category.sq"
    )
    
    var allSqldelightFilesExist = true
    sqldelightFiles.forEach { filePath ->
        val file = File(filePath)
        val exists = file.exists()
        println("  ${if (exists) "✓" else "✗"} $filePath")
        if (!exists) allSqldelightFilesExist = false
    }
    
    if (allSqldelightFilesExist) {
        println("  ✓ All SQLDelight files exist")
    } else {
        println("  ✗ Some SQLDelight files are missing")
    }
    
    // Check if models exist
    val modelFiles = listOf(
        "composeApp/src/commonMain/kotlin/com/drive/license/test/models/DatabaseQuestion.kt",
        "composeApp/src/commonMain/kotlin/com/drive/license/test/models/Book.kt",
        "composeApp/src/commonMain/kotlin/com/drive/license/test/models/QuestionCategory.kt"
    )
    
    var allModelFilesExist = true
    modelFiles.forEach { filePath ->
        val file = File(filePath)
        val exists = file.exists()
        println("  ${if (exists) "✓" else "✗"} $filePath")
        if (!exists) allModelFilesExist = false
    }
    
    if (allModelFilesExist) {
        println("  ✓ All model files exist")
    } else {
        println("  ✗ Some model files are missing")
    }
} 