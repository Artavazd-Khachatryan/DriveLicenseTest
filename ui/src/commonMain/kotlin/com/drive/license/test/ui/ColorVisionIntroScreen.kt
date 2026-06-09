package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.color_vision_disclaimer
import drivelicensetest.ui.generated.resources.color_vision_how_it_works
import drivelicensetest.ui.generated.resources.color_vision_intro_body
import drivelicensetest.ui.generated.resources.color_vision_plate_count
import drivelicensetest.ui.generated.resources.color_vision_start_button
import drivelicensetest.ui.generated.resources.color_vision_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ColorVisionIntroScreen(
    plateCount: Int,
    onStart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        topBarTitle = stringResource(Res.string.color_vision_title),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        },
    ) { inner ->
        AdaptiveContentContainer(
            modifier = modifier
                .fillMaxSize()
                .then(inner)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) { _, contentModifier ->
            Column(
                modifier = contentModifier,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.color_vision_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(Res.string.color_vision_intro_body),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = stringResource(Res.string.color_vision_plate_count, plateCount),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.color_vision_how_it_works),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(Res.string.color_vision_disclaimer),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AppButton(
                    text = stringResource(Res.string.color_vision_start_button),
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
