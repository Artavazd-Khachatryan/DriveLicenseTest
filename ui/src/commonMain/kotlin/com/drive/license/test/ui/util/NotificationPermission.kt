package com.drive.license.test.ui.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionLauncher(
    onResult: (granted: Boolean) -> Unit
): () -> Unit
