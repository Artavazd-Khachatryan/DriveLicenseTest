package com.drive.license.test.reminder

import android.content.Context
import com.drive.license.test.domain.model.ReminderSettings
import com.drive.license.test.domain.repository.ReminderPreferences

class AndroidReminderPreferences(context: Context) : ReminderPreferences {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): ReminderSettings = ReminderSettings(
        enabled = prefs.getBoolean(KEY_ENABLED, false),
        hourOfDay = prefs.getInt(KEY_HOUR, 19),
        minute = prefs.getInt(KEY_MINUTE, 0)
    )

    override fun save(settings: ReminderSettings) {
        prefs.edit()
            .putBoolean(KEY_ENABLED, settings.enabled)
            .putInt(KEY_HOUR, settings.hourOfDay)
            .putInt(KEY_MINUTE, settings.minute)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "reminder_prefs"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"
    }
}
