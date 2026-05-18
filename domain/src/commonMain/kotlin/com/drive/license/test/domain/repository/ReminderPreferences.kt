package com.drive.license.test.domain.repository

import com.drive.license.test.domain.model.ReminderSettings

interface ReminderPreferences {
    fun load(): ReminderSettings
    fun save(settings: ReminderSettings)
}
