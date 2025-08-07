package com.drive.license.test.di

import org.koin.core.context.GlobalContext

/**
 * Helper function to get Koin dependencies in a type-safe way
 */
inline fun <reified T : Any> getKoin(): T {
    return GlobalContext.get().get<T>()
} 