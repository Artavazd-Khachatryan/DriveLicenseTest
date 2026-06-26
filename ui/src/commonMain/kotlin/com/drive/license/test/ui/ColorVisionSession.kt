package com.drive.license.test.ui

import com.drive.license.test.domain.model.ColorVisionPlate
import kotlinx.datetime.Clock

data class ColorVisionSession(
    val plates: List<ColorVisionPlate>,
    val isExamSimulation: Boolean = true,
    /** When true, a passing score should continue into the theory exam simulation. */
    val leadsToTheoryExam: Boolean = false,
    val sessionId: String = Clock.System.now().toEpochMilliseconds().toString(),
    val currentIndex: Int = 0,
    val answers: MutableMap<Int, String> = mutableMapOf(),
    val isCompleted: Boolean = false,
) {
    val currentPlate: ColorVisionPlate
        get() = plates[currentIndex]

    val progress: Float
        get() = if (plates.isEmpty()) 0f else currentIndex.toFloat() / plates.size

    val isLastPlate: Boolean
        get() = currentIndex >= plates.size - 1

    val correctAnswers: Int
        get() = answers.count { (index, answer) ->
            plates.getOrNull(index)?.correctAnswer == answer
        }

    val passed: Boolean
        get() = if (plates.isEmpty()) false else when {
            isExamSimulation -> correctAnswers == plates.size
            else -> correctAnswers.toFloat() / plates.size >= 0.8f
        }

    fun answerPlate(answer: String): ColorVisionSession {
        val newAnswers = answers.toMutableMap()
        newAnswers[currentIndex] = answer
        return copy(answers = newAnswers)
    }

    fun nextPlate(): ColorVisionSession {
        return if (isLastPlate) {
            copy(isCompleted = true)
        } else {
            copy(currentIndex = currentIndex + 1)
        }
    }

    fun previousPlate(): ColorVisionSession {
        return if (currentIndex > 0) {
            copy(currentIndex = currentIndex - 1)
        } else {
            this
        }
    }
}
