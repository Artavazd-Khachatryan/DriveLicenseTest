package com.drive.license.test.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.map_title
import org.jetbrains.compose.resources.stringResource
import com.drive.license.test.ui.components.AppScaffold

@Composable
fun MapScreen(
    mapContent: @Composable (Modifier) -> Unit,
    onBack: () -> Unit
) {
    AppScaffold(
        topBarTitle = stringResource(Res.string.map_title),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(inner)
        ) {
            mapContent(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(0.dp)
            )
        }
    }
}
