package com.drive.license.test.reminder

import kotlin.random.Random

object PracticeReminderMessages {
    /** Short notification title — the rotating copy lives in [bodies]. */
    const val title = "Ժամանակն է պարապելու 🚗"

    private val bodies = listOf(
        "Այսօրվա փոքր ջանքը վաղվա մեծ հաջողությունն է։",
        "Պատրաստվեք այսօր, անցեք քննությունը վաղը։",
        "Հաղթանակը պատկանում է նրան, ով ամեն օր մի փոքր առաջ է գնում։",
        "Վաղվա հաջողությունը ծնվում է այսօրվա կարգապահությունից։",
        "Չարված գործը մնում է երազանք, արված գործը դառնում է իրականություն։",
        "Այսօրվա դժվարությունը վաղվա ուժն է։",
    )

    fun randomBody(random: Random = Random.Default): String =
        bodies[random.nextInt(bodies.size)]

    /** All notification body messages, in display order. */
    val allBodies: List<String> get() = bodies
}
