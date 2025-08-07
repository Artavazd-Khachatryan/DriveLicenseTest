package com.drive.license.test.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object KoinHelper : KoinComponent {

    inline fun <reified T : Any> get(): T {
        return inject<T>().value
    }
} 