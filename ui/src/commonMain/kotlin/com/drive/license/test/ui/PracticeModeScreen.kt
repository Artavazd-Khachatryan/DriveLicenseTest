package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.home_ai_assistant_subtitle
import drivelicensetest.ui.generated.resources.home_ai_assistant_title
import drivelicensetest.ui.generated.resources.home_chat_button
import drivelicensetest.ui.generated.resources.home_review_button
import drivelicensetest.ui.generated.resources.home_review_mistakes_count
import drivelicensetest.ui.generated.resources.home_review_mistakes_empty
import drivelicensetest.ui.generated.resources.home_review_mistakes_title
import drivelicensetest.ui.generated.resources.practice_by_category_button
import drivelicensetest.ui.generated.resources.practice_by_category_desc
import drivelicensetest.ui.generated.resources.practice_by_category_title
import drivelicensetest.ui.generated.resources.practice_exam_button
import drivelicensetest.ui.generated.resources.practice_exam_desc
import drivelicensetest.ui.generated.resources.practice_exam_title
import drivelicensetest.ui.generated.resources.practice_title
import drivelicensetest.ui.generated.resources.practice_bookmarks_button
import drivelicensetest.ui.generated.resources.practice_bookmarks_desc
import drivelicensetest.ui.generated.resources.practice_bookmarks_empty
import drivelicensetest.ui.generated.resources.practice_bookmarks_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun PracticeModeScreen(
    mistakeCount: Int,
    bookmarkCount: Int,
    onPickCategory: () -> Unit,
    onOpenMistakes: () -> Unit,
    onStartExam: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenChat: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    AppScaffold(
        topBarTitle = stringResource(Res.string.practice_title),
        navigationIcon = onBack?.let {
            {
                AppBackNavigationIcon(
                    onClick = it,
                    contentDescription = stringResource(Res.string.back),
                )
            }
        },
    ) { inner ->
        AdaptiveContentContainer(
            modifier = Modifier
                .fillMaxSize()
                .then(inner)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) { _, contentModifier ->
        Column(
            modifier = contentModifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PracticeCard(
                icon = Icons.Default.Quiz,
                accent = MaterialTheme.colorScheme.error,
                title = stringResource(Res.string.home_review_mistakes_title),
                description = if (mistakeCount > 0) {
                    stringResource(Res.string.home_review_mistakes_count, mistakeCount)
                } else {
                    stringResource(Res.string.home_review_mistakes_empty)
                },
                buttonText = stringResource(Res.string.home_review_button),
                onClick = onOpenMistakes,
            )

            PracticeCard(
                icon = Icons.Default.Category,
                accent = MaterialTheme.colorScheme.primary,
                title = stringResource(Res.string.practice_by_category_title),
                description = stringResource(Res.string.practice_by_category_desc),
                buttonText = stringResource(Res.string.practice_by_category_button),
                onClick = onPickCategory,
            )

            PracticeCard(
                icon = Icons.Default.Timer,
                accent = MaterialTheme.colorScheme.tertiary,
                title = stringResource(Res.string.practice_exam_title),
                description = stringResource(Res.string.practice_exam_desc),
                buttonText = stringResource(Res.string.practice_exam_button),
                onClick = onStartExam,
            )

            PracticeCard(
                icon = Icons.Default.Bookmark,
                accent = MaterialTheme.colorScheme.secondary,
                title = stringResource(Res.string.practice_bookmarks_title),
                description = if (bookmarkCount > 0) {
                    stringResource(Res.string.practice_bookmarks_desc)
                } else {
                    stringResource(Res.string.practice_bookmarks_empty)
                },
                buttonText = stringResource(Res.string.practice_bookmarks_button),
                onClick = onOpenBookmarks,
                enabled = bookmarkCount > 0,
            )

            if (AppFeatures.aiEnabled) {
                PracticeCard(
                    icon = Icons.Default.Chat,
                    accent = MaterialTheme.colorScheme.tertiary,
                    title = stringResource(Res.string.home_ai_assistant_title),
                    description = stringResource(Res.string.home_ai_assistant_subtitle),
                    buttonText = stringResource(Res.string.home_chat_button),
                    onClick = onOpenChat,
                )
            }
        }
        }
    }
}

@Composable
private fun PracticeCard(
    icon: ImageVector,
    accent: Color,
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.14f),
                    modifier = Modifier.size(52.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
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
