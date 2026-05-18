package com.drive.license.test.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberNotificationPermissionLauncher(
    onResult: (granted: Boolean) -> Unit
): () -> Unit = remember {
    // iOS authorization is requested by the scheduler itself when scheduling,
    // so the launcher just reports success and lets scheduling proceed.
    { onResult(true) }
}
