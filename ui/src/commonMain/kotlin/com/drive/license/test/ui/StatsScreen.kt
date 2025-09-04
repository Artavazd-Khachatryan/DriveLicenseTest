package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drive.license.test.ui.components.AppScaffold

@Composable
fun StatsScreen(onBack: () -> Unit) {
    AppScaffold(
        topBarTitle = "Statistics",
        topBarActions = {
            IconButton(onClick = onBack) {
                Text("Back", style = MaterialTheme.typography.labelMedium)
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Overall performance, categories, difficulty, trends coming soon.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}


