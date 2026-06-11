package com.drive.license.test.domain

import com.drive.license.test.domain.model.Question

/** Picks a random subset of questions from the given pool. */
object QuestionSelector {

    fun selectForPractice(
        allQuestions: List<Question>,
        count: Int,
    ): List<Question> {
        if (allQuestions.isEmpty() || count <= 0) return emptyList()
        return allQuestions.shuffled().take(count.coerceAtMost(allQuestions.size))
    }
}
