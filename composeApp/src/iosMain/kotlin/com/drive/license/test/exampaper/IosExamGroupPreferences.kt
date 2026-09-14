package com.drive.license.test.exampaper

import com.drive.license.test.domain.model.ExamPaper
import com.drive.license.test.domain.repository.ExamGroupPreferences
import platform.Foundation.NSUserDefaults

class IosExamGroupPreferences : ExamGroupPreferences {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun loadPaper(): ExamPaper {
        val stored = defaults.stringForKey(KEY_PAPER) ?: ExamPaper.ABC.name
        return runCatching { ExamPaper.valueOf(stored) }.getOrDefault(ExamPaper.ABC)
    }

    override fun savePaper(paper: ExamPaper) {
        defaults.setObject(paper.name, forKey = KEY_PAPER)
    }

    override fun hasSeenPrompt(): Boolean = defaults.boolForKey(KEY_PROMPT_SEEN)

    override fun markPromptSeen() {
        defaults.setBool(true, KEY_PROMPT_SEEN)
    }

    companion object {
        private const val KEY_PAPER = "exam_paper"
        private const val KEY_PROMPT_SEEN = "exam_paper_prompt_seen"
    }
}
