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
    fun unseenQuestionsComeFirst() {
        val all = (1..5).map { question(it) }
        val counts = mapOf(1 to 2, 2 to 1, 3 to 0, 4 to 0, 5 to 0)
        val picked = QuestionSelector.selectForPractice(all, 3, counts)
        assertEquals(listOf(3, 4, 5).sorted(), picked.map { it.id }.sorted())
    }

    @Test
    fun allQuestionsIncludedWhenCountEqualsBankSize() {
        val all = (1..4).map { question(it) }
        val picked = QuestionSelector.selectForPractice(all, 4, emptyMap())
        assertEquals(4, picked.size)
        assertEquals(all.map { it.id }.toSet(), picked.map { it.id }.toSet())
    }

    @Test
    fun countUnseen() {
        val all = (1..3).map { question(it) }
        val counts = mapOf(1 to 1, 2 to 0)
        assertEquals(2, QuestionSelector.countUnseen(all, counts))
    }
}
