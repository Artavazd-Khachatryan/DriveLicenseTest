package com.drive.license.test.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Thin wrapper over Compose's [HapticFeedback], which is backed by the platform haptic engine
 * on both Android and iOS. Keeps call sites readable (`haptics.confirm()`).
 */
class AppHaptics(private val haptic: HapticFeedback) {
    /** Light confirmation tap — e.g. selecting an answer. */
    fun confirm() = haptic.performHapticFeedback(HapticFeedbackType.LongPress)
}

@Composable
fun rememberHaptics(): AppHaptics {
    val haptic = LocalHapticFeedback.current
    return remember(haptic) { AppHaptics(haptic) }
}
