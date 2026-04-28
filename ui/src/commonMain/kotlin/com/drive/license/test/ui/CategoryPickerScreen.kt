package com.drive.license.test.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.Question
import com.drive.license.test.domain.model.QuestionCategory
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.category_picker_title
import drivelicensetest.ui.generated.resources.category_question_count
import org.jetbrains.compose.resources.stringResource

data class CategoryInfo(
    val category: QuestionCategory,
    val questionCount: Int
)

@Composable
fun CategoryPickerScreen(
    categories: List<CategoryInfo>,
    onSelectCategory: (QuestionCategory) -> Unit,
    onBack: () -> Unit
) {
    AppScaffold(
        topBarTitle = stringResource(Res.string.category_picker_title),
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 720.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(4.dp)) {
                    categories.forEachIndexed { index, info ->
                        CategoryRow(info, onClick = { onSelectCategory(info.category) })
                        if (index < categories.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(info: CategoryInfo, onClick: () -> Unit) {
    val displayName = info.category.name
        .replace("_", " ")
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = displayName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                text = stringResource(Res.string.category_question_count, info.questionCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun List<Question>.toCategoryInfoList(): List<CategoryInfo> {
    return QuestionCategory.entries
        .map { cat ->
            CategoryInfo(
                category = cat,
                questionCount = count { q -> q.categories.contains(cat) }
            )
        }
        .filter { it.questionCount > 0 }
}
