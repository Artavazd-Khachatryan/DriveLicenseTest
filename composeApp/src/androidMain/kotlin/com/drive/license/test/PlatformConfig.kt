package com.drive.license.test

actual object PlatformConfig {
    private var _anthropicApiKey: String = ""
    private var _appVersionName: String = ""

    actual val anthropicApiKey: String get() = _anthropicApiKey
    actual val appVersionName: String get() = _appVersionName

    fun init(apiKey: String, versionName: String) {
        _anthropicApiKey = apiKey
        _appVersionName = versionName
    }
}
