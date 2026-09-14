package com.drive.license.test.domain

import com.drive.license.test.domain.model.ExamPaper
import com.drive.license.test.domain.repository.ExamGroupPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** In-memory exam-paper selection; persisted via [ExamGroupPreferences]. */
class ExamPaperController(
    private val preferences: ExamGroupPreferences,
) {
    private val _paper = MutableStateFlow(preferences.loadPaper())
    val paper: StateFlow<ExamPaper> = _paper.asStateFlow()

    private val _promptPending = MutableStateFlow(!preferences.hasSeenPrompt())
    val promptPending: StateFlow<Boolean> = _promptPending.asStateFlow()

    fun completePrompt(paper: ExamPaper) {
        setPaper(paper)
        preferences.markPromptSeen()
        _promptPending.value = false
    }

    fun setPaper(paper: ExamPaper) {
        if (_paper.value == paper) return
        preferences.savePaper(paper)
        _paper.value = paper
    }
}
