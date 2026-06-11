package com.drive.license.test.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberNotificationPermissionState(
    onPermissionResult: (granted: Boolean) -> Unit,
): NotificationPermissionState {
    var granted by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.getNotificationSettingsWithCompletionHandler { settings ->
            val status = settings?.authorizationStatus
            dispatch_async(dispatch_get_main_queue()) {
                granted = status == UNAuthorizationStatusAuthorized ||
                    status == UNAuthorizationStatusProvisional ||
                    status == UNAuthorizationStatusEphemeral
            }
        }
    }

    val request: () -> Unit = remember {
        {
            val center = UNUserNotificationCenter.currentNotificationCenter()
            val options = UNAuthorizationOptionAlert or
                UNAuthorizationOptionSound or
                UNAuthorizationOptionBadge
            center.requestAuthorizationWithOptions(options) { isGranted, _ ->
                dispatch_async(dispatch_get_main_queue()) {
                    granted = isGranted
                    onPermissionResult(isGranted)
                }
            }
        }
    }

    return NotificationPermissionState(granted = granted, request = request)
}
