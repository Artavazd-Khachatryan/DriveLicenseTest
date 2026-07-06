package com.drive.license.test

import platform.Foundation.NSBundle

actual object PlatformConfig {
    actual val anthropicApiKey: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("ANTHROPIC_API_KEY") as? String ?: ""

    actual val appVersionName: String
        get() {
            val short = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
            val build = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String
            return when {
                short != null && build != null -> "$short.$build"
                short != null -> short
                else -> ""
            }
        }
}
