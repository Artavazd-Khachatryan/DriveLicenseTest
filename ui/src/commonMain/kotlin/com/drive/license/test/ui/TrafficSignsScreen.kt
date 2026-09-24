package com.drive.license.test.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.geometry.Size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drive.license.test.domain.model.TrafficSign
import com.drive.license.test.ui.components.AppBackNavigationIcon
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.util.AdaptiveContentContainer
import com.drive.license.test.ui.util.resolveDrawableResource
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.back
import drivelicensetest.ui.generated.resources.signs_category_information
import drivelicensetest.ui.generated.resources.signs_category_mandatory
import drivelicensetest.ui.generated.resources.signs_category_plate
import drivelicensetest.ui.generated.resources.signs_category_special
import drivelicensetest.ui.generated.resources.signs_category_priority
import drivelicensetest.ui.generated.resources.signs_category_prohibitory
import drivelicensetest.ui.generated.resources.signs_category_service
import drivelicensetest.ui.generated.resources.signs_category_warning
import drivelicensetest.ui.generated.resources.signs_close
import drivelicensetest.ui.generated.resources.signs_filter_all
import drivelicensetest.ui.generated.resources.signs_open
import drivelicensetest.ui.generated.resources.signs_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val signCategories = listOf(
    "warning" to Res.string.signs_category_warning,
    "priority" to Res.string.signs_category_priority,
    "prohibitory" to Res.string.signs_category_prohibitory,
    "mandatory" to Res.string.signs_category_mandatory,
    "special" to Res.string.signs_category_special,
    "information" to Res.string.signs_category_information,
    "service" to Res.string.signs_category_service,
    "plate" to Res.string.signs_category_plate,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficSignsScreen(
    signs: List<TrafficSign>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var preview by remember { mutableStateOf<TrafficSign?>(null) }
    val visible = if (selectedCategory == null) signs else signs.filter { it.category == selectedCategory }
    preview?.let { sign ->
        SignPreviewDialog(sign = sign, onDismiss = { preview = null })
    }

    AppScaffold(
        topBarTitle = stringResource(Res.string.signs_title),
        navigationIcon = {
            AppBackNavigationIcon(
                onClick = onBack,
                contentDescription = stringResource(Res.string.back),
            )
        },
    ) { inner ->
        AdaptiveContentContainer(modifier = modifier.fillMaxSize().then(inner)) { _, contentModifier ->
            Column(modifier = contentModifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text(stringResource(Res.string.signs_filter_all)) },
                    )
                    signCategories.forEach { (key, label) ->
                        if (signs.none { it.category == key }) return@forEach
                        FilterChip(
                            selected = selectedCategory == key,
                            onClick = { selectedCategory = key },
                            label = { Text(stringResource(label)) },
                        )
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(visible, key = { it.id }) { sign ->
                        SignCard(sign, onPreview = { preview = sign })
                    }
                }
            }
        }
    }
}

@Composable
private fun SignCard(sign: TrafficSign, onPreview: () -> Unit) {
    val image = resolveDrawableResource(sign.imageName)
    val openLabel = stringResource(Res.string.signs_open)
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (image != null) {
                Image(
                    painter = painterResource(image),
                    contentDescription = sign.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clickable(onClickLabel = openLabel, onClick = onPreview),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${sign.code}  ${sign.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                if (sign.description.isNotBlank() && sign.description != sign.name) {
                    Text(
                        text = sign.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SignPreviewDialog(sign: TrafficSign, onDismiss: () -> Unit) {
    val image = resolveDrawableResource(sign.imageName) ?: return
    val painter = painterResource(image)
    val size = painter.intrinsicSize
    val density = LocalDensity.current
    val hasSize = size != Size.Unspecified && size.width > 0f && size.height > 0f
    val wide = hasSize && size.width / size.height > 1.8f
    // Show the bitmap at its real pixel size. Stretching it larger is what made the preview soft.
    val nativeWidth = if (hasSize) with(density) { size.width.toDp() } else 280.dp
    val nativeHeight = if (hasSize) with(density) { size.height.toDp() } else 280.dp
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 720.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${sign.code}  ${sign.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(Res.string.signs_close),
                        )
                    }
                }
                if (wide) {
                    val height = minOf(nativeHeight, 320.dp)
                    val width = height * (size.width / size.height)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = sign.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .requiredHeight(height)
                                .requiredWidth(width),
                        )
                    }
                } else {
                    val scale = minOf(1f, 320.dp / nativeWidth, 360.dp / nativeHeight)
                    Image(
                        painter = painter,
                        contentDescription = sign.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(nativeWidth * scale)
                            .height(nativeHeight * scale)
                            .align(Alignment.CenterHorizontally),
                    )
                }
                if (sign.description.isNotBlank() && sign.description != sign.name) {
                    Text(
                        text = sign.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
        }
    }
}
