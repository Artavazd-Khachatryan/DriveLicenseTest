package com.drive.license.test.di

import com.drive.license.test.AnthropicAiAssistant
import com.drive.license.test.PlatformConfig
import com.drive.license.test.database.Database
import com.drive.license.test.database.DatabaseDriverFactory
import com.drive.license.test.database.repository.QuestionRepository
import com.drive.license.test.database.repository.UserProgressRepository
import com.drive.license.test.domain.repository.AiAssistant
import com.drive.license.test.domain.repository.QuestionRepository as DomainQuestionRepository
import com.drive.license.test.domain.repository.UserProgressRepository as DomainUserProgressRepository
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {

    single { DatabaseDriverFactory() }
    single { Database(get()) }

    single<DomainQuestionRepository> {
        QuestionRepository(get())
    }

    single<DomainUserProgressRepository> {
        UserProgressRepository(get())
    }

    single<AiAssistant> {
        AnthropicAiAssistant(PlatformConfig.anthropicApiKey)
    }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule)
} 