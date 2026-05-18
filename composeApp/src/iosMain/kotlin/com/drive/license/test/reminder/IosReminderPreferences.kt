package com.drive.license.test.reminder

import com.drive.license.test.domain.model.ReminderSettings
import com.drive.license.test.domain.repository.ReminderPreferences
import platform.Foundation.NSUserDefaults

class IosReminderPreferences : ReminderPreferences {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun load(): ReminderSettings = ReminderSettings(
        enabled = defaults.boolForKey(KEY_ENABLED),
        hourOfDay = if (defaults.objectForKey(KEY_HOUR) != null) {
            defaults.integerForKey(KEY_HOUR).toInt()
        } else 19,
        minute = defaults.integerForKey(KEY_MINUTE).toInt()
    )

    override fun save(settings: ReminderSettings) {
        defaults.setBool(settings.enabled, KEY_ENABLED)
        defaults.setInteger(settings.hourOfDay.toLong(), KEY_HOUR)
        defaults.setInteger(settings.minute.toLong(), KEY_MINUTE)
    }

    companion object {
        private const val KEY_ENABLED = "reminder_enabled"
        private const val KEY_HOUR = "reminder_hour"
        private const val KEY_MINUTE = "reminder_minute"
    }
}
