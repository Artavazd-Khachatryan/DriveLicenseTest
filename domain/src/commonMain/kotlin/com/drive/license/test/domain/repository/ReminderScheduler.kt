package com.drive.license.test.domain.repository

import com.drive.license.test.domain.model.ReminderSettings

interface ReminderScheduler {
    fun schedule(settings: ReminderSettings)
    fun cancel()
}
