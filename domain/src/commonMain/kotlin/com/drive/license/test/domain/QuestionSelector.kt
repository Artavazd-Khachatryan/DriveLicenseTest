package com.drive.license.test.domain

import com.drive.license.test.domain.model.Question

/** Picks questions from the pool, preferring unseen ones and least-attempted seen ones. */
object QuestionSelector {

    fun selectForPractice(
        allQuestions: List<Question>,
        count: Int,
        attemptCounts: Map<Int, Int> = emptyMap(),
    ): List<Question> {
        if (allQuestions.isEmpty() || count <= 0) return emptyList()
        val target = count.coerceAtMost(allQuestions.size)

        val unseen = allQuestions.filter { (attemptCounts[it.id] ?: 0) == 0 }
        val seen = allQuestions.filter { (attemptCounts[it.id] ?: 0) > 0 }

        val selected = mutableListOf<Question>()
        selected += unseen.shuffled().take(target)
        if (selected.size < target) {
            val byFewestAttempts = seen.sortedWith(
                compareBy<Question> { attemptCounts[it.id] ?: 0 }.thenBy { it.id },
            )
            selected += byFewestAttempts.take(target - selected.size)
        }
        return selected.shuffled()
    }
}
