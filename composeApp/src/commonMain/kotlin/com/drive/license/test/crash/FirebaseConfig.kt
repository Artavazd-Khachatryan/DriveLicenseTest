package com.drive.license.test.crash

expect object FirebaseConfig {
    val apiKey: String?
}

internal fun isFirebaseConfigured(apiKey: String?): Boolean {
    if (apiKey.isNullOrBlank()) return false
    if (apiKey.contains("REPLACE", ignoreCase = true)) return false
    return apiKey.length == 39 && apiKey.startsWith("A")
}
