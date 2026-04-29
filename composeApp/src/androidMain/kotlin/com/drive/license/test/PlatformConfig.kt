package com.drive.license.test

actual object PlatformConfig {
    private var _anthropicApiKey: String = ""
    actual val anthropicApiKey: String get() = _anthropicApiKey

    fun init(apiKey: String) {
        _anthropicApiKey = apiKey
    }
}
