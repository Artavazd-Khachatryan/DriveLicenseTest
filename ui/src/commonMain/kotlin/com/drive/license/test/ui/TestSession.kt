package com.drive.license.test.ui

import com.drive.license.test.domain.model.Question

data class TestSession(
    val questions: List<Question>,
    val currentQuestionIndex: Int = 0,
    val answers: MutableMap<Int, String> = mutableMapOf(),
    val isCompleted: Boolean = false
) {
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
