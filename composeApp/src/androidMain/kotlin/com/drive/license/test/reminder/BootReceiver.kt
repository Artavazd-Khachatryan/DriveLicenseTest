package com.drive.license.test.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) return

        val prefs = AndroidReminderPreferences(context.applicationContext)
        val settings = prefs.load()
        if (!settings.enabled) return
        AndroidReminderScheduler(context.applicationContext).schedule(settings)
    }
}
