import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class GenerateDatabase {
    public static void main(String[] args) {
        System.out.println("Starting database generation...");
        
        try {
            // Parse strings.xml to get string mappings
            Map<String, String> stringResources = parseStringsXml("composeApp/src/commonMain/composeResources/values/strings.xml");
            System.out.println("Parsed " + stringResources.size() + " string resources");
            
            // Extract questions from QuestionGroup files
            List<ExtractedQuestion> allQuestions = new ArrayList<>();
            
            for (int i = 1; i <= 10; i++) {
                String fileName = "composeApp/src/commonMain/kotlin/com/drive/license/test/models/QuestionGroup" + i + ".kt";
                List<ExtractedQuestion> questions = extractQuestionsFromFile(fileName, stringResources, i);
                allQuestions.addAll(questions);
                System.out.println("Extracted " + questions.size() + " questions from QuestionGroup" + i);
            }
            
            System.out.println("Total questions extracted: " + allQuestions.size());
            
            // Generate SQLite database
            String dbFile = "composeApp/src/commonMain/resources/license_test_questions.db";
            generateSqliteDatabase(allQuestions, dbFile);
            
            System.out.println("Database generated successfully: " + dbFile);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static Map<String, String> parseStringsXml(String filePath) throws IOException {
        String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
        Pattern pattern = Pattern.compile("<string name=\"([^\"]+)\">([^<]+)</string>");
        Matcher matcher = pattern.matcher(content);
        
        Map<String, String> resources = new HashMap<>();
        while (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);
            resources.put(name, value);
        }
        
        return resources;
    }
    
    static List<ExtractedQuestion> extractQuestionsFromFile(String filePath, Map<String, String> stringResources, int bookId) throws IOException {
        String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
        List<ExtractedQuestion> questions = new ArrayList<>();
        
        // Pattern to match Question objects with the correct Res.string format
        String questionPatternStr = "Question\\(\\s*" +
            "question\\s*=\\s*Res\\.string\\.([^,]+),\\s*" +
            "image\\s*=\\s*([^,]+),\\s*" +
            "answers\\s*=\\s*listOf\\(([^)]+)\\),\\s*" +
            "trueAnswer\\s*=\\s*Res\\.string\\.([^,]+),\\s*" +
            "book\\s*=\\s*Book\\.BOOK_" + bookId;
        
        Pattern questionPattern = Pattern.compile(questionPatternStr);
        Matcher matcher = questionPattern.matcher(content);
        
        while (matcher.find()) {
            String questionKey = matcher.group(1);
            String image = matcher.group(2).trim();
            String answersStr = matcher.group(3);
            String trueAnswerKey = matcher.group(4);
            
            // Extract answers
            List<String> answers = extractAnswers(answersStr, stringResources);
            
            // Get question and true answer text
            String questionText = stringResources.getOrDefault(questionKey, "MISSING: " + questionKey);
            String trueAnswerText = stringResources.getOrDefault(trueAnswerKey, "MISSING: " + trueAnswerKey);
            
            // Parse image
            String imagePath = null;
            if (!image.equals("null")) {
                if (image.startsWith("\"") && image.endsWith("\"")) {
                    imagePath = image.substring(1, image.length() - 1);
                } else {
                    imagePath = image;
                }
            }
            
            questions.add(new ExtractedQuestion(
                questionText,
                imagePath,
                answers,
                trueAnswerText,
                bookId
            ));
        }
        
        return questions;
    }
    
    static List<String> extractAnswers(String answersStr, Map<String, String> stringResources) {
        List<String> answers = new ArrayList<>();
        
        // Pattern to match Res.string.answer_key
        Pattern answerPattern = Pattern.compile("Res\\.string\\.([^,\\s]+)");
        Matcher matcher = answerPattern.matcher(answersStr);
        
        while (matcher.find()) {
            String answerKey = matcher.group(1);
            String answerText = stringResources.getOrDefault(answerKey, "MISSING: " + answerKey);
            answers.add(answerText);
        }
        
        return answers;
    }
    
    static void generateSqliteDatabase(List<ExtractedQuestion> questions, String dbFileName) throws SQLException {
        // Ensure directory exists
        File dbFile = new File(dbFileName);
        dbFile.getParentFile().mkdirs();
        
        // Remove existing database file if it exists
        if (dbFile.exists()) {
            dbFile.delete();
        }
        
        // Create database connection
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
        
        try {
            // Create tables
            createTables(connection);
            
            // Insert books
            insertBooks(connection);
            
            // Insert questions
            insertQuestions(connection, questions);
            
            System.out.println("Database created with " + questions.size() + " questions");
            
        } finally {
            connection.close();
        }
    }
    
    static void createTables(Connection connection) throws SQLException {
        String createBookTable = 
            "CREATE TABLE Book (" +
            "id INTEGER NOT NULL PRIMARY KEY," +
            "name TEXT NOT NULL" +
            ")";
        
        String createQuestionTable = 
            "CREATE TABLE Question (" +
            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "question TEXT NOT NULL," +
            "image TEXT," +
            "answers TEXT NOT NULL," +
            "true_answer TEXT NOT NULL," +
            "book_id INTEGER NOT NULL," +
            "FOREIGN KEY (book_id) REFERENCES Book(id)" +
            ")";
        
        connection.createStatement().execute(createBookTable);
        connection.createStatement().execute(createQuestionTable);
    }
    
    static void insertBooks(Connection connection) throws SQLException {
        String insertBooks = 
            "INSERT INTO Book (id, name) VALUES " +
            "(1, 'BOOK_1'), " +
            "(2, 'BOOK_2'), " +
            "(3, 'BOOK_3'), " +
            "(4, 'BOOK_4'), " +
            "(5, 'BOOK_5'), " +
            "(6, 'BOOK_6'), " +
            "(7, 'BOOK_7'), " +
            "(8, 'BOOK_8'), " +
            "(9, 'BOOK_9'), " +
            "(10, 'BOOK_10')";
        
        connection.createStatement().execute(insertBooks);
    }
    
    static void insertQuestions(Connection connection, List<ExtractedQuestion> questions) throws SQLException {
        String insertQuestion = 
            "INSERT INTO Question (question, image, answers, true_answer, book_id) " +
            "VALUES (?, ?, ?, ?, ?)";
        
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuestion);
        
        for (ExtractedQuestion question : questions) {
            String answersJson = "[\"" + String.join("\", \"", question.answers) + "\"]";
            
            preparedStatement.setString(1, question.question);
            preparedStatement.setString(2, question.image);
            preparedStatement.setString(3, answersJson);
            preparedStatement.setString(4, question.trueAnswer);
            preparedStatement.setInt(5, question.bookId);
            
            preparedStatement.executeUpdate();
        }
        
        preparedStatement.close();
    }
    
    static class ExtractedQuestion {
        String question;
        String image;
        List<String> answers;
        String trueAnswer;
        int bookId;
        
        ExtractedQuestion(String question, String image, List<String> answers, String trueAnswer, int bookId) {
            this.question = question;
            this.image = image;
            this.answers = answers;
            this.trueAnswer = trueAnswer;
            this.bookId = bookId;
        }
    }
} 