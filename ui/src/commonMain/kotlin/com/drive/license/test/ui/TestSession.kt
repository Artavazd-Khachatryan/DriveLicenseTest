package com.drive.license.test.ui

import com.drive.license.test.domain.model.Question
import kotlinx.datetime.Clock

data class TestSession(
    val questions: List<Question>,
    val sessionId: String = Clock.System.now().toEpochMilliseconds().toString(),
    val startTime: Long = Clock.System.now().toEpochMilliseconds(),
    val currentQuestionIndex: Int = 0,
    val answers: MutableMap<Int, String> = mutableMapOf(),
    val isCompleted: Boolean = false,
    val isExamMode: Boolean = false,
    val examDurationSeconds: Int = EXAM_DURATION_SECONDS
) {
    companion object {
        const val EXAM_DURATION_SECONDS = 20 * 60  // 20 minutes
        const val EXAM_QUESTION_COUNT = 30
    }
    val currentQuestion: Question
        get() = questions[currentQuestionIndex]
    
    val progress: Float
        get() = if (questions.isEmpty()) 0f else currentQuestionIndex.toFloat() / questions.size
    
    val isLastQuestion: Boolean
        get() = currentQuestionIndex >= questions.size - 1
    
    val correctAnswers: Int
        get() = answers.count { (index, answer) -> 
            questions.getOrNull(index)?.correctAnswer == answer 
        }
    
    val totalAnswered: Int
        get() = answers.size
    
    fun answerQuestion(answer: String): TestSession {
        val newAnswers = answers.toMutableMap()
        newAnswers[currentQuestionIndex] = answer
        return copy(answers = newAnswers)
    }
    
    fun nextQuestion(): TestSession {
        return if (isLastQuestion) {
            copy(isCompleted = true)
        } else {
            copy(currentQuestionIndex = currentQuestionIndex + 1)
        }
    }
    
    fun previousQuestion(): TestSession {
        return if (currentQuestionIndex > 0) {
            copy(currentQuestionIndex = currentQuestionIndex - 1)
        } else {
            this
        }
    }
}
