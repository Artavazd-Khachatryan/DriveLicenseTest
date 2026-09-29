package com.drive.license.test.ui.questioncontext

import kotlin.test.Test
import kotlin.test.assertTrue

class QuestionContextMessageTest {

    @Test
    fun messageIncludesQuestionAnswersAndFollowUp() {
        val message = buildQuestionContextMessage(
            question = "Ի՞նչ է սա",
            answers = listOf("Ա", "Բ"),
            userAnswer = "Ա",
            correctAnswer = "Բ",
            followUp = "Ինչու",
            hasImage = false,
        )
        assertTrue(message.contains("Հարց. Ի՞նչ է սա"))
        assertTrue(message.contains("1. Ա"))
        assertTrue(message.contains("2. Բ"))
        assertTrue(message.contains("Իմ պատասխանը. Ա"))
        assertTrue(message.contains("Ճիշտ պատասխանը. Բ"))
        assertTrue(message.contains("Իմ հարցը. Ինչու"))
        assertTrue(!message.contains("կցված է նկար"))
    }

    @Test
    fun messageMentionsImageWhenPresent() {
        val message = buildQuestionContextMessage(
            question = "Նշան",
            answers = listOf("Կանգ"),
            userAnswer = "Կանգ",
            correctAnswer = "Կանգ",
            followUp = "Բացատրիր",
            hasImage = true,
        )
        assertTrue(message.contains("կցված է նկար"))
    }
}
