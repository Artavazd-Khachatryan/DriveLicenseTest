package com.drive.license.test.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.drive.license.test.models.DatabaseQuestion
import com.drive.license.test.repository.QuestionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestionViewModel(
    private val questionRepository: QuestionRepository,
    private val coroutineScope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(QuestionUiState())
    val uiState: StateFlow<QuestionUiState> = _uiState.asStateFlow()
    
    var currentQuestionIndex by mutableStateOf(0)
        private set

    private val _selectedAnswers = mutableMapOf<Int, Int>()
    val selectedAnswers: Map<Int, Int> get() = _selectedAnswers.toMap()
    
    init {
        loadQuestions()
    }
    
    private fun loadQuestions() {
        coroutineScope.launch {
            questionRepository.getAllQuestions().collect { questions ->
                _uiState.value = _uiState.value.copy(
                    questions = questions,
                    isLoading = false
                )
            }
        }
    }
    
    fun selectAnswer(answerIndex: Int) {
        _selectedAnswers[currentQuestionIndex] = answerIndex
        _uiState.value = _uiState.value.copy(
            selectedAnswers = _selectedAnswers.toMap()
        )
    }
    
    fun nextQuestion() {
        val questions = _uiState.value.questions
        println("[DEBUG] nextQuestion called (current: $currentQuestionIndex, total: ${questions.size})")
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            println("[DEBUG] Moved to question: $currentQuestionIndex")
        }
    }
    
    fun previousQuestion() {
        println("[DEBUG] previousQuestion called (current: $currentQuestionIndex)")
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            println("[DEBUG] Moved to question: $currentQuestionIndex")
        }
    }
    
    fun canMoveToNext(): Boolean {
        val canMove = currentQuestionIndex < _uiState.value.questions.size - 1
        println("[DEBUG] canMoveToNext: $canMove (current: $currentQuestionIndex, total: ${_uiState.value.questions.size})")
        return canMove
    }
    
    fun canMoveToPrevious(): Boolean {
        val canMove = currentQuestionIndex > 0
        println("[DEBUG] canMoveToPrevious: $canMove (current: $currentQuestionIndex)")
        return canMove
    }
    
    fun isOnLastQuestion(): Boolean {
        return currentQuestionIndex == _uiState.value.questions.size - 1
    }
    
    fun canFinish(): Boolean {
        return isOnLastQuestion()
    }
    
    fun getCurrentQuestion(): DatabaseQuestion? {
        val questions = _uiState.value.questions
        return if (currentQuestionIndex < questions.size) {
            questions[currentQuestionIndex]
        } else null
    }
    
    fun getSelectedAnswerForCurrentQuestion(): Int? {
        return _selectedAnswers[currentQuestionIndex]
    }

    fun calculateScore(): TestScore {
        val questions = _uiState.value.questions
        var correctAnswers = 0
        var totalAnswered = 0
        
        questions.forEachIndexed { index, question ->
            val selectedAnswer = _selectedAnswers[index]
            if (selectedAnswer != null) {
                totalAnswered++
                val selectedAnswerText = question.answers[selectedAnswer]
                val correctAnswerText = question.trueAnswer
                if (selectedAnswerText == correctAnswerText) {
                    correctAnswers++
                }
            }
        }
        
        return TestScore(
            correctAnswers = correctAnswers,
            totalQuestions = questions.size,
            totalAnswered = totalAnswered
        )
    }
    
    fun resetTest() {
        currentQuestionIndex = 0
        _selectedAnswers.clear()
        _uiState.value = _uiState.value.copy(
            selectedAnswers = emptyMap()
        )
    }
}

data class QuestionUiState(
    val questions: List<DatabaseQuestion> = emptyList(),
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class TestScore(
    val correctAnswers: Int,
    val totalQuestions: Int,
    val totalAnswered: Int
) {
    val percentage: Float
        get() = if (totalAnswered > 0) (correctAnswers.toFloat() / totalAnswered) * 100 else 0f
    
    val isPassed: Boolean
        get() = percentage >= 70f
} 