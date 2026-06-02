package com.drive.license.test.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.CategoryStats
import com.drive.license.test.domain.model.Question
import com.drive.license.test.domain.model.QuestionCategory
import com.drive.license.test.domain.repository.UserProgressRepository
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.MasteryRow
import drivelicensetest.ui.generated.resources.Res
import com.drive.license.test.ui.util.formatCategoryName
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
    onBack: () -> Unit,
    userProgressRepository: UserProgressRepository? = null,
) {
    var statsByName by remember { mutableStateOf<Map<String, CategoryStats>>(emptyMap()) }
    LaunchedEffect(userProgressRepository) {
        val repo = userProgressRepository ?: return@LaunchedEffect
        statsByName = repo.getCategoryStats().associateBy { it.categoryName }
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.category_picker_title),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
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
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    categories.forEachIndexed { index, info ->
                        val stats = statsByName[info.category.name]
                        MasteryRow(
                            label = formatCategoryName(info.category.name),
                            subtitle = stringResource(Res.string.category_question_count, info.questionCount),
                            accuracy = stats?.accuracy ?: 0f,
                            attempted = stats?.attempted ?: false,
                            onClick = { onSelectCategory(info.category) },
                        )
                        if (index < categories.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
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
