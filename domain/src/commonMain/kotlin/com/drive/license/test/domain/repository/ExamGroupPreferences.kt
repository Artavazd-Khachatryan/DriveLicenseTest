package com.drive.license.test.domain.repository

import com.drive.license.test.domain.model.ExamPaper

interface ExamGroupPreferences {
    fun loadPaper(): ExamPaper
    fun savePaper(paper: ExamPaper)
    fun hasSeenPrompt(): Boolean
    fun markPromptSeen()
}
