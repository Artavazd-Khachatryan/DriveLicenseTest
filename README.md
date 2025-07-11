# Driving License Test App

A comprehensive driving license test preparation app built with Kotlin Multiplatform and Jetpack Compose.

## Features

### 🎯 Test Mode
- **Random 20-question tests** to assess your knowledge
- **Progress tracking** with real-time feedback
- **Pass/fail scoring** (70% passing threshold)
- **Answer review** after test completion
- **Unlimited retakes** to improve your score

### 📚 Practice Mode
- **Smart question selection** based on your performance
- **Difficulty-based practice** (Easy, Medium, Hard)
- **Focus on weak areas** - questions you struggle with
- **Immediate feedback** with correct answers
- **Learning progress** tracking

### 📊 Statistics & Progress Tracking
- **Overall accuracy** and performance metrics
- **Question-by-question progress** tracking
- **Learning status** (questions marked as "learned" after 3 correct answers)
- **Test session history** with detailed results
- **Achievement badges** for milestones
- **Study recommendations** based on performance

### 🤖 AI Assistant
- **Interactive chat** for questions about driving rules
- **Comprehensive rule explanations** and guidance
- **Traffic sign and marking explanations**
- **Personalized study recommendations**
- **FAQ and help section**

### 🎨 Modern UI/UX
- **Material Design 3** with beautiful theming
- **Intuitive navigation** with bottom navigation bar
- **Responsive design** for different screen sizes
- **Smooth animations** and transitions
- **Accessibility features** for all users

## Technical Architecture

### Database Structure
- **Prepopulated question database** with traffic rules and signs
- **Separate user progress tables** for tracking individual performance
- **Test session tracking** for detailed analytics
- **Question difficulty classification** based on user performance

### Key Components
- **MainScreen**: Navigation hub with bottom tabs
- **TestModeScreen**: Complete test experience with 20 random questions
- **PracticeModeScreen**: Focused practice on weak areas
- **StatisticsScreen**: Comprehensive progress tracking and analytics
- **AIAssistantScreen**: Interactive AI chat for learning support

### Data Models
- `DatabaseQuestion`: Core question data from prepopulated database
- `QuestionWithProgress`: Question combined with user progress data
- `UserStatistics`: Overall user performance metrics
- `TestSession`: Individual test attempt tracking
- `QuestionAttempt`: Detailed question response tracking

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.8+
- JDK 11+

### Installation
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on your device or emulator

### Database Setup
The app uses a prepopulated SQLite database with driving test questions. The database is automatically initialized on first launch.

## Usage Guide

### Taking a Test
1. Navigate to the **Test** tab
2. Click "Start Test" to begin a 20-question test
3. Answer each question by selecting the best option
4. Review your results and see which questions you got wrong
5. Take another test to improve your score

### Practice Mode
1. Go to the **Practice** tab
2. Choose your practice focus:
   - Questions you struggle with
   - Mixed difficulty
   - Easy questions
   - AI-recommended questions
3. Practice with immediate feedback
4. Track your improvement over time

### Viewing Statistics
1. Visit the **Stats** tab to see your progress
2. View overall accuracy and performance metrics
3. Check which questions you've learned
4. See achievement badges and recommendations

### Using AI Assistant
1. Open the **AI** tab
2. Chat with the AI about driving rules
3. Ask questions about specific traffic signs
4. Get personalized study recommendations
5. Browse the rules guide and FAQ

## Learning Algorithm

The app uses a sophisticated learning algorithm that:

1. **Tracks individual question performance** (times answered, correct/incorrect)
2. **Classifies questions by difficulty** based on user performance
3. **Marks questions as "learned"** after 3 consecutive correct answers
4. **Prioritizes practice questions** based on difficulty and learning status
5. **Provides personalized recommendations** for improvement

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Check the AI Assistant in the app
- Review the FAQ section
- Open an issue on GitHub

---

**Happy Learning! 🚗📚**