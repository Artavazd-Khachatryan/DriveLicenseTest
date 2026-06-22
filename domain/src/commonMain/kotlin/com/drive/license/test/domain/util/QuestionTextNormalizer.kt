package com.drive.license.test.domain.util

/**
 * Cleans question/answer text imported from PDF sources.
 * Collapses line breaks, replaces stray backticks with the Armenian comma, and trims whitespace.
 */
object QuestionTextNormalizer {
    fun normalize(text: String): String {
        if (text.isBlank()) return text
        return text
            .replace("::", ":")
            .replace(Regex("։{2,}"), "։") // double Armenian full stop looks like "::"
            .replace('`', '\u055D') // Armenian comma (՝)
            .replace(Regex("[\\r\\n]+"), " ")
            .replace(Regex("[ \t]+"), " ")
            .trim()
    }

    fun normalizeAnswers(answers: List<String>): List<String> =
        answers.map(::normalize)
}
