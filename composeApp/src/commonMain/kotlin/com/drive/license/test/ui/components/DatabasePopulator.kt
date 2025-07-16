package com.drive.license.test.ui.components

import androidx.compose.runtime.*
import com.drive.license.test.models.*
import com.drive.license.test.repository.QuestionRepository
import org.jetbrains.compose.resources.stringResource

@Composable
fun PopulateQuestionsOnce(
    questionRepository: QuestionRepository,
    onComplete: () -> Unit = {}
) {
    var didPopulate by remember { mutableStateOf(false) }

    // Compose context: resolve all strings here
    val allQuestions = QuestionGroup1.questions +
        QuestionGroup2.questions +
        QuestionGroup3.questions +
        QuestionGroup4.questions +
        QuestionGroup5.questions +
        QuestionGroup6.questions +
        QuestionGroup7.questions +
        QuestionGroup8.questions +
        QuestionGroup9.questions +
        QuestionGroup10.questions

    val dbQuestions = allQuestions.map { question ->
        DatabaseQuestion(
            id = 0,
            question = stringResource(question.question),
            image = question.image,
            answers = question.answers.map { stringResource(it) },
            trueAnswer = stringResource(question.trueAnswer),
            book = question.book,
            categories = question.categories
        )
    }

    LaunchedEffect(dbQuestions) {
        if (!didPopulate) {
            println("[DEBUG_LOG] Clearing existing database...")
            questionRepository.deleteAllQuestions()
            println("[DEBUG_LOG] Database cleared, now populating with resolved strings...")
            
            dbQuestions.forEach { dbQuestion ->
                questionRepository.insertDatabaseQuestion(dbQuestion)
            }
            didPopulate = true
            onComplete()
        }
    }
} 