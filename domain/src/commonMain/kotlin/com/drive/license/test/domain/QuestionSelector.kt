package com.drive.license.test.domain

import com.drive.license.test.domain.model.Question

/**
 * Picks practice questions so unseen items are always served before repeats.
 * After every question has been attempted at least once, cycles through the full bank again.
 */
object QuestionSelector {

    fun selectForPractice(
        allQuestions: List<Question>,
        count: Int,
        timesAnsweredByQuestionId: Map<Int, Int>,
    ): List<Question> {
        if (allQuestions.isEmpty() || count <= 0) return emptyList()
        val limit = count.coerceAtMost(allQuestions.size)

        val unseen = allQuestions
            .filter { (timesAnsweredByQuestionId[it.id] ?: 0) == 0 }
            .shuffled()
        val seen = allQuestions
            .filter { (timesAnsweredByQuestionId[it.id] ?: 0) > 0 }
            .shuffled()

        return (unseen + seen).take(limit)
    }

    fun countUnseen(
        allQuestions: List<Question>,
        timesAnsweredByQuestionId: Map<Int, Int>,
    ): Int = allQuestions.count { (timesAnsweredByQuestionId[it.id] ?: 0) == 0 }
}
