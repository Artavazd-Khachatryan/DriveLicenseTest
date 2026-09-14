package com.drive.license.test.domain.model

/** User-facing exam paper: A/B/C or D/T. */
enum class ExamPaper {
    ABC,
    DT,
}

/**
 * How a question is tagged in the bank.
 * GENERAL is included for every paper; ABC/DT are exclusive.
 */
enum class QuestionExamGroup {
    GENERAL,
    ABC,
    DT,
    ;

    fun isVisibleFor(paper: ExamPaper): Boolean =
        this == GENERAL || name == paper.name

    companion object {
        /** Content is validated by scripts/verify_questions.py; an unknown tag is a content bug. */
        fun fromDb(value: String): QuestionExamGroup =
            entries.find { it.name == value }
                ?: error("Unknown exam_group '$value'; expected one of ${entries.map { it.name }}")
    }
}
