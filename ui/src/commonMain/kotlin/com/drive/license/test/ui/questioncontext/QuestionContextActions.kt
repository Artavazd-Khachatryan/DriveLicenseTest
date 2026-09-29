package com.drive.license.test.ui.questioncontext

import androidx.compose.runtime.Composable

interface QuestionContextActions {
    fun copy(text: String)
}

@Composable
expect fun rememberQuestionContextActions(): QuestionContextActions
