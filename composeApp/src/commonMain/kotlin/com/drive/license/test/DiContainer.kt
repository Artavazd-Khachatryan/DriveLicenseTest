package com.drive.license.test

import com.drive.license.test.database.Database
import com.drive.license.test.database.DatabaseDriverFactory
import com.drive.license.test.domain.repository.QuestionRepository

/**
 * Dependency Injection Container
 * 
 * This follows clean architecture principles:
 * - composeApp acts as the DI container
 * - Creates implementations and passes domain interfaces to UI
 * - UI layer only knows about domain interfaces
 */
object DiContainer {
    
    private val databaseDriverFactory by lazy { DatabaseDriverFactory() }
    private val database by lazy { Database(databaseDriverFactory) }
    
    /**
     * Creates and returns the QuestionRepository implementation
     * cast to the domain interface
     */
    val questionRepository: QuestionRepository by lazy {
        com.drive.license.test.database.repository.QuestionRepository(database) as QuestionRepository
    }
    
    /**
     * Provides access to the database for initialization
     */
    val databaseForInitialization: Database by lazy { database }
} 