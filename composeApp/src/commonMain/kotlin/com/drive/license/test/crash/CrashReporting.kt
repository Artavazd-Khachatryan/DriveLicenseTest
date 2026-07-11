package com.drive.license.test.crash

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.initialize

object CrashReporting {

    private var initialized = false
    private var enabled = false

    fun initialize() {
        if (initialized) return
        initialized = true
        if (!isFirebaseConfigured(FirebaseConfig.apiKey)) return
        Firebase.initialize()
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
        enabled = true
        installPlatformCrashHandlers()
    }

    fun log(message: String) {
        if (!enabled) return
        runCatching { Firebase.crashlytics.log(message) }
    }

    fun recordException(throwable: Throwable, message: String? = null) {
        if (!enabled) return
        runCatching {
            message?.let { Firebase.crashlytics.log(it) }
            Firebase.crashlytics.recordException(throwable)
        }
    }
}
