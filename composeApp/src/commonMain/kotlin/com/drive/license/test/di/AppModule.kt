package com.drive.license.test.di

import com.drive.license.test.database.Database
import com.drive.license.test.database.DatabaseDriverFactory
import com.drive.license.test.database.DatabaseInitializer
import com.drive.license.test.database.repository.QuestionRepository
import com.drive.license.test.domain.repository.QuestionRepository as DomainQuestionRepository
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Koin DI Module for the application
 * 
 * This follows clean architecture principles:
 * - Domain interfaces are provided as singletons
 * - Database implementations are provided as singletons
 * - UI components get domain interfaces, not implementations
 */
val appModule = module {
    
    // ✅ Database Layer
    single { DatabaseDriverFactory() }
    single { Database(get()) }
    
    // ✅ Repository Implementation (implements domain interface)
    single<DomainQuestionRepository> { 
        QuestionRepository(get()) 
    }
}

/**
 * Initialize Koin DI
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule)
} 