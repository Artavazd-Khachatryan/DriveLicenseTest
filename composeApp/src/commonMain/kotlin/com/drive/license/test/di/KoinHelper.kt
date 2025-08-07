package com.drive.license.test.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Koin component for dependency injection
 */
object KoinHelper : KoinComponent {
    
    /**
     * Get a dependency from Koin
     */
    inline fun <reified T : Any> get(): T {
        return inject<T>().value
    }
} 