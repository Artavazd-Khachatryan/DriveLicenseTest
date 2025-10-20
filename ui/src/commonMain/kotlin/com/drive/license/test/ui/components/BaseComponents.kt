package com.drive.license.test.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    topBarTitle: String? = null,
    topBarActions: @Composable (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit)? = null,
    content: @Composable (innerPadding: Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            if (topBarTitle != null || navigationIcon != null) {
                TopAppBar(
                    title = { if (topBarTitle != null) Text(text = topBarTitle) },
                    navigationIcon = { navigationIcon?.invoke() },
                    actions = { topBarActions?.invoke() }
                )
            }
        },
        bottomBar = { bottomBar?.invoke() },
        floatingActionButton = { floatingActionButton?.let { it() } }
    ) { inner ->
        content(Modifier.padding(inner))
    }
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) { Text(text) }
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) { content() }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    containerColor: Color,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(12.dp)) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = contentColor)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = contentColor.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    description: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    AppCard(modifier = modifier, containerColor = containerColor) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(contentPadding)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = contentColor)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            AppButton(text = actionText, onClick = onAction, modifier = Modifier.fillMaxWidth())
        }
    }
}


