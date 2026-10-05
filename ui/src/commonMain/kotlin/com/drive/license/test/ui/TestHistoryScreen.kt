package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.TestSessionSummary
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.stats_no_history
import drivelicensetest.ui.generated.resources.stats_tap_to_review
import drivelicensetest.ui.generated.resources.stats_test_history
import org.jetbrains.compose.resources.stringResource

@Composable
fun TestHistoryScreen(
    userProgressRepository: UserProgressRepository,
    onOpenSessionReview: (String) -> Unit,
    onBack: () -> Unit,
) {
    var history by remember { mutableStateOf<List<TestSessionSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        history = userProgressRepository.getTestHistory()
        isLoading = false
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.stats_test_history),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        },
    ) { inner ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().then(inner),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            AdaptiveContentContainer(
                modifier = Modifier
                    .fillMaxSize()
                    .then(inner)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) { _, contentModifier ->
                AppCard(modifier = contentModifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (history.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.stats_no_history),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.stats_tap_to_review),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            history.forEachIndexed { index, session ->
                                TestHistoryRow(
                                    session = session,
                                    onClick = { onOpenSessionReview(session.id) },
                                )
                                if (index < history.lastIndex) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
