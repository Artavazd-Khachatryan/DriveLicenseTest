package com.drive.license.test.reminder

import com.drive.license.test.domain.model.ReminderSettings
import com.drive.license.test.domain.repository.ReminderScheduler
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

class IosReminderScheduler : ReminderScheduler {

    override fun schedule(settings: ReminderSettings) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(listOf(REQUEST_ID))
        if (!settings.enabled) return

        val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        center.requestAuthorizationWithOptions(options) { granted, _ ->
            if (!granted) return@requestAuthorizationWithOptions
            postRequest(settings)
        }
    }

    override fun cancel() {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(listOf(REQUEST_ID))
    }

    private fun postRequest(settings: ReminderSettings) {
        val content = UNMutableNotificationContent().apply {
            setTitle(TITLE)
            setBody(BODY)
        }
        val components = NSDateComponents().apply {
            hour = settings.hourOfDay.toLong()
            minute = settings.minute.toLong()
        }
        val trigger = UNCalendarNotificationTrigger
            .triggerWithDateMatchingComponents(components, repeats = true)

        val request = UNNotificationRequest.requestWithIdentifier(
            REQUEST_ID,
            content,
            trigger
        )
        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request, withCompletionHandler = null)
    }

    companion object {
        private const val REQUEST_ID = "practice_reminder_daily"
        private const val TITLE = "Ժամանակն է պարապելու 🚗"
        private const val BODY = "Մի քանի հարց այսօր ձեզ կպահեն քննությանը պատրաստ։ Հավատում եմ ձեզ։"
    }
}
