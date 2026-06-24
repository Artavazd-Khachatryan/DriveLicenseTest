package com.drive.license.test.domain

import com.drive.license.test.domain.model.Book
import com.drive.license.test.domain.model.Question
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuestionSelectorTest {

    private fun question(id: Int) = Question(
        id = id,
        question = "Q$id",
        answers = listOf("a", "b"),
        correctAnswer = "a",
        imageUrl = null,
        categories = emptyList(),
        book = Book.BOOK_1
    )

    @Test
    fun returnsRequestedCount() {
        val all = (1..5).map { question(it) }
        val picked = QuestionSelector.selectForPractice(all, 3)
        assertEquals(3, picked.size)
        assertEquals(3, picked.map { it.id }.toSet().size)
    }

    @Test
    fun allQuestionsIncludedWhenCountEqualsBankSize() {
        val all = (1..4).map { question(it) }
        val picked = QuestionSelector.selectForPractice(all, 4)
        assertEquals(4, picked.size)
        assertEquals(all.map { it.id }.toSet(), picked.map { it.id }.toSet())
    }

    @Test
    fun emptyPoolReturnsEmpty() {
        assertTrue(QuestionSelector.selectForPractice(emptyList(), 10).isEmpty())
    }

    @Test
    fun prefersUnseenQuestions() {
        val all = (1..6).map { question(it) }
        val attemptCounts = mapOf(1 to 3, 2 to 1, 3 to 2)
        val picked = QuestionSelector.selectForPractice(all, 3, attemptCounts)
        assertEquals(3, picked.size)
        assertEquals(setOf(4, 5, 6), picked.map { it.id }.toSet())
    }

    @Test
    fun fillsWithLeastAttemptedSeenWhenUnseenExhausted() {
        val all = (1..4).map { question(it) }
        val attemptCounts = mapOf(1 to 5, 2 to 1, 3 to 3, 4 to 2)
        val picked = QuestionSelector.selectForPractice(all, 3, attemptCounts)
        assertEquals(3, picked.size)
        assertEquals(setOf(2, 4, 3), picked.map { it.id }.toSet())
    }
}
