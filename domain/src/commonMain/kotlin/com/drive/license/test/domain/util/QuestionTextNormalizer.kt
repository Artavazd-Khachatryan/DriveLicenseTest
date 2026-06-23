package com.drive.license.test.domain.util

/**
 * Cleans question/answer text imported from PDF sources.
 * Collapses line breaks, replaces stray backticks with the Armenian comma, and trims whitespace.
 */
object QuestionTextNormalizer {
    fun normalize(text: String): String {
        if (text.isBlank()) return text
        return stripLeadingDot(
            text
                .replace("::", ":")
                .replace(Regex("։{2,}"), "։") // double Armenian full stop looks like "::"
                .replace('`', '\u055D') // Armenian comma (՝)
                .replace(Regex("[\\r\\n]+"), " ")
                .replace(Regex("[ \t]+"), " ")
                .trim()
        )
    }

    /** Removes a stray leading period from PDF import (e.g. ".Միայն Բ" → "Միայն Բ"). */
    private fun stripLeadingDot(text: String): String {
        if (text.length < 2 || text[0] != '.') return text
        val next = text[1]
        if (next.isDigit()) return text
        return text.drop(1).trimStart()
    }

    fun normalizeAnswers(answers: List<String>): List<String> =
        answers.map(::normalize)
}
