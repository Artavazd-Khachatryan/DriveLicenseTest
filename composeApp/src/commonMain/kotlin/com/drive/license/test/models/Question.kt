package com.drive.license.test.models

import org.jetbrains.compose.resources.StringResource

class Question(
    val question: StringResource,
    val image: String?,
    val answers: List<StringResource>,
    val trueAnswer: StringResource
)