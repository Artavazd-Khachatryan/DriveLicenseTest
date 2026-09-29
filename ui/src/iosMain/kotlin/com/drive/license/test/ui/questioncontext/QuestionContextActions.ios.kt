package com.drive.license.test.ui.questioncontext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIPasteboard

@Composable
actual fun rememberQuestionContextActions(): QuestionContextActions {
    return remember { IosQuestionContextActions() }
}

private class IosQuestionContextActions : QuestionContextActions {
    override fun copy(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}
