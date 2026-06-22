package com.drive.license.test.reminder

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PracticeReminderMessagesTest {

    @Test
    fun title_isSet() {
        assertEquals("Ժամանակն է պարապելու 🚗", PracticeReminderMessages.title)
    }

    @Test
    fun randomBody_returnsOneOfSevenMessages() {
        val allBodies = setOf(
            "Այսօրվա փոքր ջանքը վաղվա մեծ հաջողությունն է։",
            "Փոքր ջանքերն ամեն օր՝ մեծ հաջողություն քննության օրը։",
            "Պատրաստվիր այսօր, անցիր քննությունը վաղը։",
            "Հաղթանակը պատկանում է նրան, ով ամեն օր մի փոքր առաջ է գնում։",
            "Վաղվա հաջողությունը ծնվում է այսօրվա կարգապահությունից։",
            "Չարված գործը մնում է երազանք, արված գործը դառնում է իրականություն։",
            "Այսօրվա դժվարությունը վաղվա ուժն է։",
        )
        repeat(20) { i ->
            val body = PracticeReminderMessages.randomBody(Random(i))
            assertTrue(body in allBodies, "Unexpected body: $body")
        }
    }
}
