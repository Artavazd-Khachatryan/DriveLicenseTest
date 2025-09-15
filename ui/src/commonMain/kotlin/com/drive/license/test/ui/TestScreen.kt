package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.repository.QuestionRepository
import com.drive.license.test.domain.model.Question
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.AppButton

@Composable
fun TestScreen(
    questionRepository: QuestionRepository,
    onBack: () -> Unit,
    onStartSession: (List<Question>) -> Unit
) {
    val allQuestions by questionRepository.getAllQuestions().collectAsState(initial = emptyList())
    val testQuestions = allQuestions.shuffled().take(20)

    AppScaffold(topBarTitle = "20-Question Test") { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Ready to start your test?",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "You will be given 20 random questions from different categories. Take your time and read each question carefully.",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AppButton(
                text = "Start Test",
                onClick = { onStartSession(testQuestions) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
