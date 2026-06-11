package com.drive.license.test.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.ReminderSettings
import com.drive.license.test.domain.repository.ReminderPreferences
import com.drive.license.test.domain.repository.ReminderScheduler
import com.drive.license.test.domain.repository.UserProgressRepository
import kotlinx.coroutines.launch
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.ThemeModeSelector
import com.drive.license.test.ui.util.AdaptiveContentContainer
import com.drive.license.test.ui.util.rememberNotificationPermissionLauncher
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.settings_dialog_cancel
import drivelicensetest.ui.generated.resources.settings_dialog_ok
import drivelicensetest.ui.generated.resources.settings_reminder_change_time
import drivelicensetest.ui.generated.resources.settings_reminder_permission_denied
import drivelicensetest.ui.generated.resources.settings_reminder_pick_time_hint
import drivelicensetest.ui.generated.resources.settings_reminder_pick_time_title
import drivelicensetest.ui.generated.resources.settings_reminder_subtitle
import drivelicensetest.ui.generated.resources.settings_reminder_time_label
import drivelicensetest.ui.generated.resources.settings_reminder_title
import drivelicensetest.ui.generated.resources.settings_reset_button
import drivelicensetest.ui.generated.resources.settings_reset_dialog_message
import drivelicensetest.ui.generated.resources.settings_reset_dialog_title
import drivelicensetest.ui.generated.resources.settings_reset_subtitle
import drivelicensetest.ui.generated.resources.settings_reset_title
import drivelicensetest.ui.generated.resources.settings_theme_subtitle
import drivelicensetest.ui.generated.resources.settings_theme_title
import drivelicensetest.ui.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    reminderPreferences: ReminderPreferences,
    reminderScheduler: ReminderScheduler,
    userProgressRepository: UserProgressRepository,
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onStatisticsReset: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var settings by remember { mutableStateOf(ReminderSettings()) }
    var loaded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        settings = reminderPreferences.load()
        loaded = true
    }

    fun persist(updated: ReminderSettings, reschedule: Boolean = true) {
        settings = updated
        reminderPreferences.save(updated)
        if (reschedule) {
            reminderScheduler.schedule(updated)
        }
    }

    val requestPermission = rememberNotificationPermissionLauncher { granted ->
        if (granted) {
            permissionDenied = false
            persist(settings.copy(enabled = true))
        } else {
            permissionDenied = true
            persist(settings.copy(enabled = false))
        }
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.settings_title),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        }
    ) { inner ->
        AdaptiveContentContainer(
            modifier = modifier
                .fillMaxSize()
                .then(inner)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) { _, contentModifier ->
            Column(
                modifier = contentModifier,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!loaded) return@Column

                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(Res.string.settings_theme_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(Res.string.settings_theme_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ThemeModeSelector(
                            isDarkTheme = isDarkTheme,
                            onDarkThemeChange = onDarkThemeChange,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(Res.string.settings_reminder_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(Res.string.settings_reminder_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = settings.enabled,
                                onCheckedChange = { wantEnabled ->
                                    if (wantEnabled) {
                                        requestPermission()
                                    } else {
                                        permissionDenied = false
                                        persist(settings.copy(enabled = false))
                                    }
                                }
                            )
                        }

                        if (permissionDenied) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(Res.string.settings_reminder_permission_denied),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(16.dp))

                        ReminderTimeRow(
                            hour = settings.hourOfDay,
                            minute = settings.minute,
                            enabled = settings.enabled,
                            onOpenTimePicker = { showTimePicker = true }
                        )
                    }
                }

                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(Res.string.settings_reset_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(Res.string.settings_reset_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        AppButton(
                            text = stringResource(Res.string.settings_reset_button),
                            onClick = { showResetDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        )
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = settings.hourOfDay,
            initialMinute = settings.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(stringResource(Res.string.settings_reminder_pick_time_title)) },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    persist(
                        settings.copy(
                            hourOfDay = timeState.hour,
                            minute = timeState.minute
                        ),
                        reschedule = settings.enabled
                    )
                }) { Text(stringResource(Res.string.settings_dialog_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(Res.string.settings_dialog_cancel))
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = timeState)
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(Res.string.settings_reset_dialog_title)) },
            text = { Text(stringResource(Res.string.settings_reset_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    coroutineScope.launch {
                        userProgressRepository.resetStatistics()
                        onStatisticsReset()
                    }
                }) { Text(stringResource(Res.string.settings_dialog_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(Res.string.settings_dialog_cancel))
                }
            }
        )
    }
}

@Composable
private fun ReminderTimeRow(
    hour: Int,
    minute: Int,
    enabled: Boolean,
    onOpenTimePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeLabel = formatTime(hour, minute)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenTimePicker)
            .semantics { role = Role.Button },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.settings_reminder_time_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timeLabel,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (!enabled) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.settings_reminder_pick_time_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        TextButton(onClick = onOpenTimePicker) {
            Text(stringResource(Res.string.settings_reminder_change_time))
        }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val h = hour.toString().padStart(2, '0')
    val m = minute.toString().padStart(2, '0')
    return "$h:$m"
}
