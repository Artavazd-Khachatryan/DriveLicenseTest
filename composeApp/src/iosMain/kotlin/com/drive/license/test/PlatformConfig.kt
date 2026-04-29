package com.drive.license.test

import platform.Foundation.NSBundle

actual object PlatformConfig {
    actual val anthropicApiKey: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("ANTHROPIC_API_KEY") as? String ?: ""
}
