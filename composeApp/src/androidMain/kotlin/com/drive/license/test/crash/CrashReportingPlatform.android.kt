package com.drive.license.test.crash

internal actual fun installPlatformCrashHandlers() {
    val previous = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        CrashReporting.recordException(throwable, "uncaught.${thread.name}")
        previous?.uncaughtException(thread, throwable)
    }
}
