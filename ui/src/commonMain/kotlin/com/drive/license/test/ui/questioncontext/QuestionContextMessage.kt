package com.drive.license.test.ui.questioncontext

fun buildQuestionContextMessage(
    question: String,
    answers: List<String>,
    userAnswer: String,
    correctAnswer: String,
    followUp: String,
    hasImage: Boolean,
): String = buildString {
    appendLine("Սա Հայաստանի վարորդական իրավունքի թեստի հարց է։")
    appendLine("Պատասխանիր հայերեն, պարզ, և պատրաստ եղիր շարունակել զրույցը։")
    if (hasImage) {
        appendLine("Հարցին կցված է նկար։ Եթե նկարը չես տեսնում, ասա այդ մասին։")
    }
    appendLine()
    appendLine("Հարց. $question")
    appendLine("Տարբերակներ.")
    answers.forEachIndexed { index, answer ->
        appendLine("${index + 1}. $answer")
    }
    appendLine()
    appendLine("Իմ պատասխանը. $userAnswer")
    appendLine("Ճիշտ պատասխանը. $correctAnswer")
    appendLine()
    append("Իմ հարցը. $followUp")
}
