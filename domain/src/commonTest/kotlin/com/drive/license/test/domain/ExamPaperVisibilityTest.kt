package com.drive.license.test.domain

import com.drive.license.test.domain.model.ExamPaper
import com.drive.license.test.domain.model.QuestionExamGroup
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExamPaperVisibilityTest {

    @Test
    fun generalIsVisibleOnBothPapers() {
        assertTrue(QuestionExamGroup.GENERAL.isVisibleFor(ExamPaper.ABC))
        assertTrue(QuestionExamGroup.GENERAL.isVisibleFor(ExamPaper.DT))
    }

    @Test
    fun exclusiveGroupsStayOnTheirPaper() {
        assertTrue(QuestionExamGroup.ABC.isVisibleFor(ExamPaper.ABC))
        assertFalse(QuestionExamGroup.ABC.isVisibleFor(ExamPaper.DT))
        assertTrue(QuestionExamGroup.DT.isVisibleFor(ExamPaper.DT))
        assertFalse(QuestionExamGroup.DT.isVisibleFor(ExamPaper.ABC))
    }
}
