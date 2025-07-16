#!/usr/bin/env kotlin

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

fun main() {
    println("Starting database population script...")
    
    // Parse strings.xml to get string mappings
    val stringResources = parseStringsXml("composeApp/src/commonMain/composeResources/values/strings.xml")
    println("Parsed ${stringResources.size} string resources")
    
    // Extract questions from QuestionGroup files
    val allQuestions = mutableListOf<ExtractedQuestion>()
    
    for (i in 1..10) {
        val fileName = "composeApp/src/commonMain/kotlin/com/drive/license/test/models/QuestionGroup$i.kt"
        val questions = extractQuestionsFromFile(fileName, stringResources, i)
        allQuestions.addAll(questions)
        println("Extracted ${questions.size} questions from QuestionGroup$i")
    }
    
    println("Total questions extracted: ${allQuestions.size}")
    
    // Generate SQL insert statements
    val sqlFile = "database_inserts.sql"
    generateSqlInserts(allQuestions, sqlFile)
    
    // Generate Kotlin data class for direct insertion
    val kotlinFile = "DatabaseQuestions.kt"
    generateKotlinDataClass(allQuestions, kotlinFile)
    
    println("Database population files generated:")
    println("- $sqlFile: SQL insert statements")
    println("- $kotlinFile: Kotlin data class for direct insertion")
}

data class ExtractedQuestion(
    val id: Int,
    val question: String,
    val image: String?,
    val answers: List<String>,
    val trueAnswer: String,
    val bookId: Int
)

fun parseStringsXml(filePath: String): Map<String, String> {
    val content = File(filePath).readText()
    val pattern = Pattern.compile("<string name=\"([^\"]+)\">([^<]+)</string>")
    val matcher = pattern.matcher(content)
    
    val resources = mutableMapOf<String, String>()
    while (matcher.find()) {
        val name = matcher.group(1)
        val value = matcher.group(2)
        resources[name] = value
    }
    
    return resources
}

fun extractQuestionsFromFile(filePath: String, stringResources: Map<String, String>, bookId: Int): List<ExtractedQuestion> {
    val content = File(filePath).readText()
    val questions = mutableListOf<ExtractedQuestion>()
    
    // Pattern to match Question objects
    val questionPattern = Pattern.compile(
        "Question\\(\\s*" +
        "question\\s*=\\s*Res\\.string\\.([^,]+),\\s*" +
        "image\\s*=\\s*([^,]+),\\s*" +
        "answers\\s*=\\s*listOf\\(([^)]+)\\),\\s*" +
        "trueAnswer\\s*=\\s*Res\\.string\\.([^,]+),\\s*" +
        "book\\s*=\\s*Book\\.BOOK_$bookId"
    )
    
    val matcher = questionPattern.matcher(content)
    var questionId = (bookId - 1) * 100 + 1
    
    while (matcher.find()) {
        val questionKey = matcher.group(1)
        val image = matcher.group(2).trim()
        val answersStr = matcher.group(3)
        val trueAnswerKey = matcher.group(4)
        
        // Extract answers
        val answers = extractAnswers(answersStr, stringResources)
        
        // Get question and true answer text
        val questionText = stringResources[questionKey] ?: "MISSING: $questionKey"
        val trueAnswerText = stringResources[trueAnswerKey] ?: "MISSING: $trueAnswerKey"
        
        // Parse image
        val imagePath = when {
            image == "null" -> null
            image.startsWith("\"") && image.endsWith("\"") -> image.substring(1, image.length - 1)
            else -> image
        }
        
        questions.add(ExtractedQuestion(
            id = questionId++,
            question = questionText,
            image = imagePath,
            answers = answers,
            trueAnswer = trueAnswerText,
            bookId = bookId
        ))
    }
    
    return questions
}

fun extractAnswers(answersStr: String, stringResources: Map<String, String>): List<String> {
    val answers = mutableListOf<String>()
    
    // Pattern to match Res.string.answer_key
    val answerPattern = Pattern.compile("Res\\.string\\.([^,\\s]+)")
    val matcher = answerPattern.matcher(answersStr)
    
    while (matcher.find()) {
        val answerKey = matcher.group(1)
        val answerText = stringResources[answerKey] ?: "MISSING: $answerKey"
        answers.add(answerText)
    }
    
    return answers
}

fun generateSqlInserts(questions: List<ExtractedQuestion>, outputFile: String) {
    PrintWriter(FileWriter(outputFile)).use { writer ->
        writer.println("-- Database insert statements for questions")
        writer.println("-- Generated automatically from QuestionGroup files")
        writer.println()
        
        for (question in questions) {
            val answersJson = question.answers.joinToString("\", \"") { it.replace("\"", "\\\"") }
            val imageValue = if (question.image != null) "'${question.image}'" else "NULL"
            
            writer.println("INSERT INTO Question (id, question, image, answers, true_answer, book_id) VALUES (")
            writer.println("  ${question.id},")
            writer.println("  '${question.question.replace("'", "''")}',")
            writer.println("  $imageValue,")
            writer.println("  '[\"$answersJson\"]',")
            writer.println("  '${question.trueAnswer.replace("'", "''")}',")
            writer.println("  ${question.bookId}")
            writer.println(");")
            writer.println()
        }
    }
}

fun generateKotlinDataClass(questions: List<ExtractedQuestion>, outputFile: String) {
    PrintWriter(FileWriter(outputFile)).use { writer ->
        writer.println("package com.drive.license.test.database")
        writer.println()
        writer.println("import com.drive.license.test.models.Book")
        writer.println("import com.drive.license.test.models.DatabaseQuestion")
        writer.println()
        writer.println("object PrepopulatedQuestions {")
        writer.println("    val questions = listOf(")
        
        for ((index, question) in questions.withIndex()) {
            val answersStr = question.answers.joinToString(",\n            ") { "\"${it.replace("\"", "\\\"")}\"" }
            val imageStr = if (question.image != null) "\"${question.image}\"" else "null"
            val bookStr = "Book.BOOK_${question.bookId}"
            
            writer.println("        DatabaseQuestion(")
            writer.println("            id = ${question.id},")
            writer.println("            question = \"${question.question.replace("\"", "\\\"")}\",")
            writer.println("            image = $imageStr,")
            writer.println("            answers = listOf(")
            writer.println("                $answersStr")
            writer.println("            ),")
            writer.println("            trueAnswer = \"${question.trueAnswer.replace("\"", "\\\"")}\",")
            writer.println("            book = $bookStr,")
            writer.println("            categories = emptyList()")
            writer.println("        )${if (index < questions.size - 1) "," else ""}")
        }
        
        writer.println("    )")
        writer.println("}")
    }
} 