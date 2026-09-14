package com.drive.license.test.exampaper

import android.content.Context
import com.drive.license.test.domain.model.ExamPaper
import com.drive.license.test.domain.repository.ExamGroupPreferences

class AndroidExamGroupPreferences(context: Context) : ExamGroupPreferences {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun loadPaper(): ExamPaper {
        val stored = prefs.getString(KEY_PAPER, ExamPaper.ABC.name) ?: ExamPaper.ABC.name
        return runCatching { ExamPaper.valueOf(stored) }.getOrDefault(ExamPaper.ABC)
    }

    override fun savePaper(paper: ExamPaper) {
        prefs.edit().putString(KEY_PAPER, paper.name).apply()
    }

    override fun hasSeenPrompt(): Boolean = prefs.getBoolean(KEY_PROMPT_SEEN, false)

    override fun markPromptSeen() {
        prefs.edit().putBoolean(KEY_PROMPT_SEEN, true).apply()
    }

    companion object {
        private const val PREFS_NAME = "exam_group_prefs"
        private const val KEY_PAPER = "exam_paper"
        private const val KEY_PROMPT_SEEN = "exam_paper_prompt_seen"
    }
}
