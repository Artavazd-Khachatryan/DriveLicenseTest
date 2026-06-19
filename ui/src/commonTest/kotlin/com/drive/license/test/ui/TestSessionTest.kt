package com.drive.license.test.ui

import com.drive.license.test.domain.model.Book
import com.drive.license.test.domain.model.Question
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSessionTest {

    private fun question(id: Int, correct: String = "a") = Question(
        id = id,
        question = "Q$id",
        answers = listOf("a", "b"),
        correctAnswer = correct,
        book = Book.BOOK_1,
    )

    @Test
    fun failedQuestionsReturnsOnlyIncorrectOrUnanswered() {
        val questions = listOf(question(1), question(2), question(3))
        val session = TestSession(
            questions = questions,
            answers = mutableMapOf(
                0 to "a",
                1 to "b",
            ),
        )

        assertEquals(listOf(2), session.failedQuestions.map { it.id })
    }

    @Test
    fun failedQuestionsIsEmptyWhenAllCorrect() {
        val questions = listOf(question(1), question(2))
        val session = TestSession(
            questions = questions,
            answers = mutableMapOf(
                0 to "a",
                1 to "a",
            ),
        )

        assertEquals(emptyList(), session.failedQuestions)
    }
}
