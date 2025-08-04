# Database Setup for Driving License Test App

## Overview
This app uses a pre-populated SQLite database containing driving license test questions. The database is automatically initialized on first launch and contains comprehensive traffic rules and sign questions.

## Database Structure

### Core Tables
- **Question**: Main question table with traffic rules and sign questions
- **Book**: Reference table for question categorization
- **Category**: Question categorization system

### Database Features
- Pre-populated with real driving test questions
- Automatic initialization on app startup
- Cross-platform compatibility (Android, iOS, Desktop)
- Optimized for performance with proper indexing

## Implementation Details

### Database Initialization
The database is automatically initialized in `DatabaseInitializer.kt`:
- Checks if database exists and contains questions
- Uses pre-populated database file from resources
- Handles platform-specific database loading

### Platform-Specific Loading
- **Android**: Database loaded from assets folder
- **iOS**: Database copied from app bundle to documents directory
- **Desktop**: Database loaded from resources directory

### Repository Pattern
The `QuestionRepository` provides a clean interface for:
- Getting all questions
- Retrieving questions by ID
- Filtering questions by book/category
- Managing question data

## File Structure
```
src/commonMain/resources/
└── license_test_questions.db    # Pre-populated database

src/commonMain/kotlin/com/drive/license/test/database/
├── Database.kt                  # Main database interface
├── DatabaseDriverFactory.kt     # Platform-specific drivers
└── DatabaseInitializer.kt       # Database initialization logic

src/commonMain/kotlin/com/drive/license/test/repository/
└── QuestionRepository.kt        # Data access layer
```

## Usage
The database is automatically used by the app's UI components:
- Questions are loaded in `QuestionViewModel`
- Displayed in `QuestionScreen` component
- Managed through `QuestionRepository`

No manual database setup is required - the app handles everything automatically on first launch. 