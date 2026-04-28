package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold

@Composable
fun PracticeModeScreen(
    weakAreaCount: Int,
    onPickCategory: () -> Unit,
    onStartWeakAreas: () -> Unit,
    onStartExam: () -> Unit,
    onBack: () -> Unit,
    bottomBar: @Composable (() -> Unit)? = null
) {
    AppScaffold(
        topBarTitle = "Practice",
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        bottomBar = bottomBar
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(inner)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 720.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PracticeCard(
                icon = { Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary) },
                title = "By Category",
                description = "Focus on a specific topic: traffic signs, right of way, maneuvers, and more.",
                buttonText = "Choose Category",
                onClick = onPickCategory,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            )

            PracticeCard(
                icon = { Icon(Icons.Default.TrendingDown, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.error) },
                title = "Weak Areas",
                description = if (weakAreaCount > 0)
                    "$weakAreaCount question${if (weakAreaCount == 1) "" else "s"} where you need more practice."
                else
                    "Complete a test first to identify your weak areas.",
                buttonText = "Start Practice",
                onClick = onStartWeakAreas,
                enabled = weakAreaCount > 0,
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            )

            PracticeCard(
                icon = { Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.tertiary) },
                title = "Exam Simulation",
                description = "30 questions · 20 minutes · Pass at 70%. Mirrors the real Armenian driving exam.",
                buttonText = "Start Exam",
                onClick = onStartExam,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun PracticeCard(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface
) {
    AppCard(modifier = Modifier.fillMaxWidth(), containerColor = containerColor) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                icon()
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            AppButton(text = buttonText, onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth())
        }
    }
}
