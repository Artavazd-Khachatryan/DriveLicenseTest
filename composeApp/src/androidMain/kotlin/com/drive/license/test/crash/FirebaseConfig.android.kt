package com.drive.license.test.crash

import com.drive.license.test.database.appContext

actual object FirebaseConfig {
    actual val apiKey: String?
        get() = runCatching {
            val resId = appContext.resources.getIdentifier("google_api_key", "string", appContext.packageName)
            if (resId == 0) null else appContext.getString(resId)
        }.getOrNull()
}
