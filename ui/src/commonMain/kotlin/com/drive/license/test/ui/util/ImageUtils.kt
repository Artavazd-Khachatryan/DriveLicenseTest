package com.drive.license.test.ui.util

import com.drive.license.test.domain.model.Question
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.DrawableResource

fun resolveQuestionImage(question: Question): DrawableResource? {
    return question.imageUrl?.let { resolveDrawableResource(it) }
        ?: resolveDrawableResource("question${question.id}_image.webp")
        ?: resolveDrawableResource("question${question.id}_image.png")
}

fun resolveDrawableResource(resourceName: String?): DrawableResource? {
    if (resourceName.isNullOrBlank()) {
        println("Drawable resource name is null or blank")
        return null
    }
    val key = resourceName.substringBeforeLast('.')
    val found = Res.allDrawableResources[key]
    if (found == null) {
        println("Drawable resource not found: '$key' (from input: '$resourceName')")
        val suggestions = Res.allDrawableResources.keys
            .filter { it.contains(key, ignoreCase = true) || key.contains(it, ignoreCase = true) }
            .take(5)
        if (suggestions.isNotEmpty()) {
            println("   Suggestions: ${suggestions.joinToString()}")
        }
    }
    return found
}


