package com.drive.license.test.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
data class NotificationPermissionState(
    /** `null` while the platform permission status is still loading (iOS). */
    val granted: Boolean?,
    val request: () -> Unit,
)

@Composable
expect fun rememberNotificationPermissionState(
    onPermissionResult: (granted: Boolean) -> Unit = {},
): NotificationPermissionState
