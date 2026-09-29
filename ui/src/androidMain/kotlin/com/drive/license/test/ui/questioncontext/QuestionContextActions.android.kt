package com.drive.license.test.ui.questioncontext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberQuestionContextActions(): QuestionContextActions {
    val context = LocalContext.current
    return remember(context) { AndroidQuestionContextActions(context) }
}

private class AndroidQuestionContextActions(
    private val context: Context,
) : QuestionContextActions {

    override fun copy(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("question-context", text))
    }
}
