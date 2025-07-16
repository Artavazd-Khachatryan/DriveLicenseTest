#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import java.io.File

/**
 * Simple script to populate database by iterating through each QuestionGroup
 * and saving questions directly to the database.
 */

// Question data class
data class Question(
    val question: String,
    val image: String?,
    val answers: List<String>,
    val trueAnswer: String,
    val book: String,
    val categories: List<String> = emptyList()
)

// Database operations
class DatabasePopulator {
    private val json = Json { ignoreUnknownKeys = true }
    
    fun answersToJson(answers: List<String>): String {
        return json.encodeToString(ListSerializer(String.serializer()), answers)
    }
    
    fun insertQuestion(question: Question, questionId: Long) {
        val answersJson = answersToJson(question.answers)
        
        println("Inserting Question ID: $questionId")
        println("  Question: ${question.question}")
        println("  Image: ${question.image}")
        println("  Answers: ${question.answers}")
        println("  True Answer: ${question.trueAnswer}")
        println("  Book: ${question.book}")
        println("  Categories: ${question.categories}")
        println("  Answers JSON: $answersJson")
        println("---")
    }
    
    fun generateSqlInsert(question: Question, questionId: Long): String {
        val answersJson = answersToJson(question.answers)
        val bookId = question.book.replace("BOOK_", "").toInt()
        
        return "INSERT INTO Question (id, question, image, answers, true_answer, book_id) VALUES ($questionId, '${escapeSql(question.question)}', '${question.image ?: ""}', '$answersJson', '${escapeSql(question.trueAnswer)}', $bookId);"
    }
    
    private fun escapeSql(str: String): String {
        return str.replace("'", "''")
    }
}

// QuestionGroup data extractor
class QuestionGroupExtractor {
    fun extractQuestionsFromFile(filePath: String): List<Question> {
        val questions = mutableListOf<Question>()
        
        try {
            val file = File(filePath)
            if (!file.exists()) {
                println("Warning: QuestionGroup file not found at $filePath")
                return questions
            }
            
            val content = file.readText()
            
            // Extract questions using regex patterns
            val questionPattern = """Question\(\s*question\s*=\s*Res\.string\.(\w+),\s*image\s*=\s*"([^"]*)",\s*answers\s*=\s*listOf\(\s*([^)]+)\s*\),\s*trueAnswer\s*=\s*Res\.string\.(\w+),\s*book\s*=\s*Book\.(\w+),\s*categories\s*=\s*listOf\(\s*([^)]+)\s*\)\s*\)""".toRegex(RegexOption.DOT_MATCHES_ALL)
            
            val matches = questionPattern.findAll(content)
            matches.forEach { matchResult ->
                val questionRes = matchResult.groupValues[1]
                val image = matchResult.groupValues[2]
                val answersText = matchResult.groupValues[3]
                val trueAnswerRes = matchResult.groupValues[4]
                val book = matchResult.groupValues[5]
                val categoriesText = matchResult.groupValues[6]
                
                // Parse answers
                val answers = parseAnswers(answersText)
                
                // Parse categories
                val categories = parseCategories(categoriesText)
                
                questions.add(Question(
                    question = questionRes,
                    image = if (image.isNotEmpty()) image else null,
                    answers = answers,
                    trueAnswer = trueAnswerRes,
                    book = book,
                    categories = categories
                ))
            }
            
            println("Extracted ${questions.size} questions from $filePath")
            
        } catch (e: Exception) {
            println("Error extracting questions from $filePath: ${e.message}")
        }
        
        return questions
    }
    
    private fun parseAnswers(answersText: String): List<String> {
        val answerPattern = """Res\.string\.(\w+)""".toRegex()
        return answerPattern.findAll(answersText).map { it.groupValues[1] }.toList()
    }
    
    private fun parseCategories(categoriesText: String): List<String> {
        val categoryPattern = """QuestionCategory\.(\w+)""".toRegex()
        return categoryPattern.findAll(categoriesText).map { it.groupValues[1] }.toList()
    }
}

fun main() = runBlocking {
    println("Starting simple database population script...")
    
    val extractor = QuestionGroupExtractor()
    val populator = DatabasePopulator()
    
    // Extract questions from all QuestionGroup files
    val allQuestions = mutableListOf<Question>()
    
    for (i in 1..10) {
        val filePath = "composeApp/src/commonMain/kotlin/com/drive/license/test/models/QuestionGroup$i.kt"
        val questions = extractor.extractQuestionsFromFile(filePath)
        allQuestions.addAll(questions)
    }
    
    println("Total questions extracted: ${allQuestions.size}")
    
    // Generate SQL statements
    val sqlBuilder = StringBuilder()
    
    // Insert books
    sqlBuilder.appendLine("-- Insert books")
    for (i in 1..10) {
        sqlBuilder.appendLine("INSERT INTO Book (id, name) VALUES ($i, 'BOOK_$i');")
    }
    sqlBuilder.appendLine()
    
    // Insert categories
    sqlBuilder.appendLine("-- Insert categories")
    val categories = listOf(
        "TRAFFIC_SIGNS_AND_MARKINGS",
        "INTERSECTIONS_AND_CROSSINGS", 
        "LANE_USAGE_AND_POSITIONING",
        "MANEUVERS_AND_TURNS",
        "RIGHT_OF_WAY_AND_PRIORITY",
        "PROHIBITED_ACTIONS",
        "SPECIAL_VEHICLES_AND_SITUATIONS",
        "ROAD_CONDITIONS_AND_VISIBILITY",
        "VEHICLE_TYPES_AND_CATEGORIES",
        "GENERAL_TRAFFIC_RULES"
    )
    
    categories.forEachIndexed { index, category ->
        sqlBuilder.appendLine("INSERT INTO QuestionCategory (id, name) VALUES (${index + 1}, '$category');")
    }
    sqlBuilder.appendLine()
    
    // Insert questions
    sqlBuilder.appendLine("-- Insert questions")
    allQuestions.forEachIndexed { index, question ->
        val questionId = index + 1
        val sqlInsert = populator.generateSqlInsert(question, questionId.toLong())
        sqlBuilder.appendLine(sqlInsert)
    }
    sqlBuilder.appendLine()
    
    // Insert question-category junctions
    sqlBuilder.appendLine("-- Insert question-category junctions")
    allQuestions.forEachIndexed { index, question ->
        val questionId = index + 1
        question.categories.forEach { category ->
            val categoryId = categories.indexOf(category) + 1
            sqlBuilder.appendLine("INSERT INTO QuestionCategoryJunction (question_id, category_id) VALUES ($questionId, $categoryId);")
        }
    }
    
    // Write SQL to file
    val sqlFile = File("database_insert_statements.sql")
    sqlFile.writeText(sqlBuilder.toString())
    
    println("SQL statements written to: ${sqlFile.absolutePath}")
    println("Database population script completed!")
    
    // Print sample of extracted questions
    println("\nSample extracted questions:")
    allQuestions.take(3).forEachIndexed { index, question ->
        println("Question ${index + 1}:")
        println("  Question resource: ${question.question}")
        println("  Image: ${question.image}")
        println("  Answers: ${question.answers}")
        println("  True answer resource: ${question.trueAnswer}")
        println("  Book: ${question.book}")
        println("  Categories: ${question.categories}")
        println("---")
    }
} 