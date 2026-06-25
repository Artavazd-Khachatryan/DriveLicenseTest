package com.drive.license.test.crash

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
internal actual fun installPlatformCrashHandlers() {
    setUnhandledExceptionHook { throwable ->
        runCatching {
            CrashReporting.recordException(throwable, "kotlin.unhandled")
        }
        terminateWithUnhandledException(throwable)
    }
}
