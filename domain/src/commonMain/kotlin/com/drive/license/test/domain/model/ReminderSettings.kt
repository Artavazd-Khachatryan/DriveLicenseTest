package com.drive.license.test.domain.model

data class ReminderSettings(
    val enabled: Boolean = false,
    val hourOfDay: Int = 19,
    val minute: Int = 0
)
