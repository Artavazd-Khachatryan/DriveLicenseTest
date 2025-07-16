#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.regex.Pattern

/**
 * Fixed script to create a prepopulated database by extracting actual string values
 * from StringResource references in QuestionGroup files
 */

fun main() {
    println("Starting prepopulated database creation...")
    
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
    
    // Generate prepopulated SQLite database
    val dbFile = "composeApp/src/commonMain/resources/license_test_questions.db"
    generatePrepopulatedDatabase(allQuestions, dbFile)
    
    println("Prepopulated database created successfully: $dbFile")
}

data class ExtractedQuestion(
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

fun generatePrepopulatedDatabase(questions: List<ExtractedQuestion>, dbFileName: String) {
    // Ensure directory exists
    val dbFile = File(dbFileName)
    dbFile.parentFile.mkdirs()
    
    // Remove existing database file if it exists
    if (dbFile.exists()) {
        dbFile.delete()
    }
    
    // Create database connection
    val connection = DriverManager.getConnection("jdbc:sqlite:$dbFileName")
    
    try {
        // Create tables
        createTables(connection)
        
        // Insert books
        insertBooks(connection)
        
        // Insert questions
        insertQuestions(connection, questions)
        
        println("Prepopulated database created with ${questions.size} questions")
        
    } finally {
        connection.close()
    }
}

fun createTables(connection: Connection) {
    val createBookTable = """
        CREATE TABLE Book (
            id INTEGER NOT NULL PRIMARY KEY,
            name TEXT NOT NULL
        )
    """.trimIndent()
    
    val createQuestionTable = """
        CREATE TABLE Question (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            question TEXT NOT NULL,
            image TEXT,
            answers TEXT NOT NULL,
            true_answer TEXT NOT NULL,
            book_id INTEGER NOT NULL,
            FOREIGN KEY (book_id) REFERENCES Book(id)
        )
    """.trimIndent()
    
    val createCategoryTable = """
        CREATE TABLE QuestionCategory (
            id INTEGER NOT NULL PRIMARY KEY,
            name TEXT NOT NULL
        )
    """.trimIndent()
    
    val createJunctionTable = """
        CREATE TABLE QuestionCategoryJunction (
            question_id INTEGER NOT NULL,
            category_id INTEGER NOT NULL,
            PRIMARY KEY (question_id, category_id),
            FOREIGN KEY (question_id) REFERENCES Question(id),
            FOREIGN KEY (category_id) REFERENCES QuestionCategory(id)
        )
    """.trimIndent()
    
    connection.createStatement().execute(createBookTable)
    connection.createStatement().execute(createQuestionTable)
    connection.createStatement().execute(createCategoryTable)
    connection.createStatement().execute(createJunctionTable)
}

fun insertBooks(connection: Connection) {
    val insertBooks = """
        INSERT INTO Book (id, name) VALUES 
        (1, 'BOOK_1'),
        (2, 'BOOK_2'),
        (3, 'BOOK_3'),
        (4, 'BOOK_4'),
        (5, 'BOOK_5'),
        (6, 'BOOK_6'),
        (7, 'BOOK_7'),
        (8, 'BOOK_8'),
        (9, 'BOOK_9'),
        (10, 'BOOK_10')
    """.trimIndent()
    
    connection.createStatement().execute(insertBooks)
}

fun insertQuestions(connection: Connection, questions: List<ExtractedQuestion>) {
    val json = Json { ignoreUnknownKeys = true }
    
    val insertQuestion = """
        INSERT INTO Question (question, image, answers, true_answer, book_id)
        VALUES (?, ?, ?, ?, ?)
    """.trimIndent()
    
    val preparedStatement = connection.prepareStatement(insertQuestion)
    
    for (question in questions) {
        val answersJson = json.encodeToString(ListSerializer(String.serializer()), question.answers)
        
        preparedStatement.setString(1, question.question)
        preparedStatement.setString(2, question.image)
        preparedStatement.setString(3, answersJson)
        preparedStatement.setString(4, question.trueAnswer)
        preparedStatement.setInt(5, question.bookId)
        
        preparedStatement.executeUpdate()
    }
    
    preparedStatement.close()
} 