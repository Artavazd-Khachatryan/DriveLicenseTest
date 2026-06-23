package com.drive.license.test.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals

class QuestionTextNormalizerTest {

    @Test
    fun collapsesNewlinesAndWhitespace() {
        val raw = """
            |Խաչմերուկում կազմակերպված է շրջանաձեւ երթեւեկություն։
            |        Երթեւեկելի մասը, որով մոտենում եք
        """.trimMargin()
        assertEquals(
            "Խաչմերուկում կազմակերպված է շրջանաձեւ երթեւեկություն։ Երթեւեկելի մասը, որով մոտենում եք",
            QuestionTextNormalizer.normalize(raw),
        )
    }

    @Test
    fun replacesBacktickWithArmenianComma() {
        assertEquals(
            "Ուղիղ եւ դեպի ձախ\u055D մուտք",
            QuestionTextNormalizer.normalize("Ուղիղ եւ դեպի ձախ` մուտք"),
        )
    }

    @Test
    fun collapsesDoubleColon() {
        assertEquals("մի: երկ", QuestionTextNormalizer.normalize("մի:: երկ"))
    }

    @Test
    fun collapsesDoubleArmenianFullStop() {
        assertEquals(
            "Միայն շրջանցման դեպքում։",
            QuestionTextNormalizer.normalize("Միայն շրջանցման դեպքում։։"),
        )
    }

    @Test
    fun stripsLeadingDotBeforeText() {
        assertEquals(
            "Միայն Բ",
            QuestionTextNormalizer.normalize(".Միայն Բ"),
        )
    }

    @Test
    fun keepsDecimalNumbers() {
        assertEquals("3.5 մ։", QuestionTextNormalizer.normalize("3.5 մ։"))
    }
}
