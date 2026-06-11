package com.drive.license.test.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.LearningCenter
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.SectionHeader
import com.drive.license.test.ui.components.StatChip
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.driving_schools_city_section
import drivelicensetest.ui.generated.resources.driving_schools_stat_label
import drivelicensetest.ui.generated.resources.driving_schools_filter_all
import drivelicensetest.ui.generated.resources.driving_schools_intro
import drivelicensetest.ui.generated.resources.driving_schools_label_address
import drivelicensetest.ui.generated.resources.driving_schools_label_phone
import drivelicensetest.ui.generated.resources.map_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrivingSchoolsScreen(
    schools: List<LearningCenter>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cities = remember(schools) { schools.map { it.city }.distinct().sorted() }
    var selectedCity by remember { mutableStateOf<String?>(null) }

    val filteredSchools = remember(schools, selectedCity) {
        if (selectedCity == null) schools else schools.filter { it.city == selectedCity }
    }
    val groupedByCity = remember(filteredSchools, selectedCity) {
        if (selectedCity != null) {
            mapOf(selectedCity!! to filteredSchools)
        } else {
            filteredSchools.groupBy { it.city }
                .entries
                .sortedBy { it.key }
                .associate { it.key to it.value }
        }
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.map_title),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        }
    ) { inner ->
        AdaptiveContentContainer(
            modifier = modifier.fillMaxSize().then(inner)
        ) { _, contentModifier ->
            LazyColumn(
                modifier = contentModifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    DrivingSchoolsHeroCard(schoolCount = schools.size)
                }

                if (cities.size > 1) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedCity == null,
                                onClick = { selectedCity = null },
                                label = { Text(stringResource(Res.string.driving_schools_filter_all)) },
                                leadingIcon = if (selectedCity == null) {
                                    { Icon(Icons.Default.School, contentDescription = null, Modifier.size(18.dp)) }
                                } else null,
                            )
                            cities.forEach { city ->
                                FilterChip(
                                    selected = selectedCity == city,
                                    onClick = { selectedCity = if (selectedCity == city) null else city },
                                    label = { Text(city) },
                                )
                            }
                        }
                    }
                }

                groupedByCity.forEach { (city, citySchools) ->
                    if (selectedCity == null && cities.size > 1) {
                        item(key = "header-$city") {
                            SectionHeader(
                                title = stringResource(
                                    Res.string.driving_schools_city_section,
                                    city,
                                    citySchools.size
                                ),
                                icon = Icons.Default.LocationOn,
                                iconTint = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                    items(citySchools, key = { "${it.name}-${it.address}" }) { school ->
                        DrivingSchoolCard(school = school)
                    }
                }
            }
        }
    }
}

@Composable
private fun DrivingSchoolsHeroCard(schoolCount: Int) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(Res.string.map_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(Res.string.driving_schools_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                )
            }
            StatChip(
                label = stringResource(Res.string.driving_schools_stat_label),
                value = schoolCount.toString(),
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun DrivingSchoolCard(school: LearningCenter) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.size(44.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = school.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = school.city,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            if (school.description.isNotBlank()) {
                Text(
                    text = school.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            ContactInfoRow(
                icon = Icons.Default.LocationOn,
                label = stringResource(Res.string.driving_schools_label_address),
                value = school.address,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            ContactInfoRow(
                icon = Icons.Default.Phone,
                label = stringResource(Res.string.driving_schools_label_phone),
                value = school.phone,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    containerColor: androidx.compose.ui.graphics.Color,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
