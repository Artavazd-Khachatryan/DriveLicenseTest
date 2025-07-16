#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
@file:DependsOn("org.xerial:sqlite-jdbc:3.42.0.0")

import java.sql.*

fun main() {
    println("Testing database system...")
    
    try {
        Class.forName("org.sqlite.JDBC")
        val connection = DriverManager.getConnection("jdbc:sqlite:composeApp/src/commonMain/resources/license_test_questions.db")
        
        // Check table structure
        println("=== Table Structure ===")
        val metaData = connection.metaData
        val columns = metaData.getColumns(null, null, "Question", null)
        while (columns.next()) {
            println("Column: ${columns.getString("COLUMN_NAME")} | Type: ${columns.getString("TYPE_NAME")}")
        }
        columns.close()
        
        // Check question count
        println("\n=== Question Count ===")
        val countStmt = connection.createStatement()
        val countRs = countStmt.executeQuery("SELECT COUNT(*) FROM Question")
        if (countRs.next()) {
            println("Total questions: ${countRs.getInt(1)}")
        }
        countRs.close()
        countStmt.close()
        
        // Check first few questions
        println("\n=== Sample Questions ===")
        val stmt = connection.createStatement()
        val rs = stmt.executeQuery("SELECT * FROM Question LIMIT 3")
        val rsMetaData = rs.metaData
        val columnCount = rsMetaData.columnCount
        
        while (rs.next()) {
            println("Question ID: ${rs.getInt("id")}")
            println("Question: ${rs.getString("question")}")
            println("Answers: ${rs.getString("answers")}")
            println("True Answer: ${rs.getString("true_answer")}")
            println("Book ID: ${rs.getInt("book_id")}")
            println("---")
        }
        rs.close()
        stmt.close()
        
        // Check for any null or empty values
        println("\n=== Data Quality Check ===")
        val qualityStmt = connection.createStatement()
        val qualityRs = qualityStmt.executeQuery("""
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN question IS NULL OR question = '' THEN 1 ELSE 0 END) as null_questions,
                SUM(CASE WHEN answers IS NULL OR answers = '' THEN 1 ELSE 0 END) as null_answers,
                SUM(CASE WHEN true_answer IS NULL OR true_answer = '' THEN 1 ELSE 0 END) as null_true_answers
            FROM Question
        """)
        
        if (qualityRs.next()) {
            println("Total questions: ${qualityRs.getInt("total")}")
            println("Null/empty questions: ${qualityRs.getInt("null_questions")}")
            println("Null/empty answers: ${qualityRs.getInt("null_answers")}")
            println("Null/empty true answers: ${qualityRs.getInt("null_true_answers")}")
        }
        qualityRs.close()
        qualityStmt.close()
        
        connection.close()
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    }
} 