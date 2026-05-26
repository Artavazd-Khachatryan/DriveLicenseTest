package com.drive.license.test.crash

import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfFile

actual object FirebaseConfig {
    actual val apiKey: String?
        get() {
            val path = NSBundle.mainBundle.pathForResource("GoogleService-Info", null) ?: return null
            val data = NSData.dataWithContentsOfFile(path) ?: return null
            val content = NSString.create(data, NSUTF8StringEncoding)?.toString() ?: return null
            if (content.contains("REPLACE_WITH_FIREBASE")) return null
            return Regex("""<key>API_KEY</key>\s*<string>([^<]+)</string>""")
                .find(content)
                ?.groupValues
                ?.get(1)
        }
}
