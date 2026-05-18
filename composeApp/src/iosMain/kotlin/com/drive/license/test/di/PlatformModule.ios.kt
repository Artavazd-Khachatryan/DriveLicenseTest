package com.drive.license.test.di

import com.drive.license.test.domain.repository.ReminderPreferences
import com.drive.license.test.domain.repository.ReminderScheduler
import com.drive.license.test.reminder.IosReminderPreferences
import com.drive.license.test.reminder.IosReminderScheduler
import org.koin.dsl.module

actual val platformModule = module {
    single<ReminderPreferences> { IosReminderPreferences() }
    single<ReminderScheduler> { IosReminderScheduler() }
}
