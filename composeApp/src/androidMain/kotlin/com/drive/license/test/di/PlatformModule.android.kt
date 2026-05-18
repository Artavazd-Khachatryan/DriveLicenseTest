package com.drive.license.test.di

import com.drive.license.test.database.appContext
import com.drive.license.test.domain.repository.ReminderPreferences
import com.drive.license.test.domain.repository.ReminderScheduler
import com.drive.license.test.reminder.AndroidReminderPreferences
import com.drive.license.test.reminder.AndroidReminderScheduler
import org.koin.dsl.module

actual val platformModule = module {
    single<ReminderPreferences> { AndroidReminderPreferences(appContext) }
    single<ReminderScheduler> { AndroidReminderScheduler(appContext) }
}
