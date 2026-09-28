package com.drive.license.test.ui.crossing

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.AppOutlinedButton
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.crossing_back
import drivelicensetest.ui.generated.resources.crossing_check
import drivelicensetest.ui.generated.resources.crossing_clear
import drivelicensetest.ui.generated.resources.crossing_crash
import drivelicensetest.ui.generated.resources.crossing_pick_all
import drivelicensetest.ui.generated.resources.crossing_result_correct
import drivelicensetest.ui.generated.resources.crossing_result_wrong
import drivelicensetest.ui.generated.resources.crossing_correct
import drivelicensetest.ui.generated.resources.crossing_next
import drivelicensetest.ui.generated.resources.crossing_playing
import drivelicensetest.ui.generated.resources.crossing_previous
import drivelicensetest.ui.generated.resources.crossing_progress
import drivelicensetest.ui.generated.resources.crossing_prompt
import drivelicensetest.ui.generated.resources.crossing_reason_blue
import drivelicensetest.ui.generated.resources.crossing_reason_left
import drivelicensetest.ui.generated.resources.crossing_reason_light
import drivelicensetest.ui.generated.resources.crossing_reason_main
import drivelicensetest.ui.generated.resources.crossing_reason_arrow
import drivelicensetest.ui.generated.resources.crossing_reason_right
import drivelicensetest.ui.generated.resources.crossing_right_order
import drivelicensetest.ui.generated.resources.crossing_show
import drivelicensetest.ui.generated.resources.crossing_title
import drivelicensetest.ui.generated.resources.crossing_wrong
import drivelicensetest.ui.generated.resources.crossing_your_order
import drivelicensetest.ui.generated.resources.crossing_your_order_empty
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CrossingDemoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var deck by remember { mutableStateOf(CrossingScenarios.shuffled()) }
    var index by remember { mutableIntStateOf(0) }
    val scenario = deck[index]
    val crossingOrder = scenario.crossingOrder.map { id ->
        scenario.vehicles.first { it.id == id }
    }
    var picks by remember(scenario.id) { mutableStateOf<List<String>>(emptyList()) }
    var progress by remember(scenario.id) { mutableStateOf<Map<String, Float>>(emptyMap()) }
    var activeId by remember(scenario.id) { mutableStateOf<String?>(null) }
    var playing by remember(scenario.id) { mutableStateOf(false) }
    var revealed by remember(scenario.id) { mutableStateOf(false) }
    var crashIds by remember(scenario.id) { mutableStateOf<Set<String>>(emptySet()) }
    val scope = rememberCoroutineScope()
    var playJob by remember { mutableStateOf<Job?>(null) }
    var generation by remember { mutableIntStateOf(0) }

    fun stopAndReset() {
        generation += 1
        playJob?.cancel()
        playJob = null
        playing = false
        activeId = null
        crashIds = emptySet()
        progress = emptyMap()
    }

    fun goTo(target: Int) {
        if (playing || target !in deck.indices || target == index) return
        stopAndReset()
        picks = emptyList()
        revealed = false
        index = target
    }

    suspend fun move(id: String, target: Float, duration: Int, generationAtStart: Int) {
        if (generation != generationAtStart) return
        activeId = id
        val anim = Animatable(progress[id] ?: 0f)
        anim.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing),
        ) {
            if (generation != generationAtStart) return@animateTo
            progress = progress + (id to value)
        }
    }

    fun playAnswer() {
        val legalIds = crossingOrder.map { it.id }
        val isCorrect = picks == legalIds
        generation += 1
        val generationAtStart = generation
        playJob?.cancel()
        playJob = scope.launch {
            playing = true
            revealed = true
            crashIds = emptySet()
            progress = scenario.vehicles.associate { it.id to 0f }
            if (isCorrect) {
                crossingOrder.forEachIndexed { step, vehicle ->
                    if (generation != generationAtStart) return@launch
                    move(vehicle.id, 1f, 1400, generationAtStart)
                    if (step < crossingOrder.lastIndex) delay(220)
                }
            } else {
                val clash = firstClash(picks, legalIds)
                activeId = null
                val together = Animatable(0f)
                together.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
                ) {
                    if (generation != generationAtStart) return@animateTo
                    progress = progress +
                        (clash.wrongId to value * 0.5f) +
                        (clash.priorityId to value * 0.55f)
                }
                if (generation != generationAtStart) return@launch
                crashIds = setOf(clash.wrongId, clash.priorityId)
                delay(900)
            }
            if (generation != generationAtStart) return@launch
            activeId = null
            playing = false
        }
    }

    LaunchedEffect(scenario.id) {
        stopAndReset()
        revealed = false
    }

    val pickIndex = picks.withIndex().associate { it.value to it.index + 1 }
    val legalIds = crossingOrder.map { it.id }
    val ready = picks.size == scenario.vehicles.size
    val correct = picks == legalIds
    val clash = if (revealed && !correct && picks.isNotEmpty()) firstClash(picks, legalIds) else null
    val explanation = crossingExplanation(scenario)

    val canGoPrevious = index > 0 && !playing
    val canGoNext = index < deck.lastIndex && !playing
    val pickedVehicles = picks.map { id -> scenario.vehicles.first { it.id == id } }

    AppScaffold(
        topBarTitle = stringResource(Res.string.crossing_title),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.crossing_back),
                )
            }
        },
    ) { inner ->
        AdaptiveContentContainer(
            modifier = modifier.fillMaxSize().then(inner),
        ) { _, contentModifier ->
            Column(
                modifier = contentModifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QuestionPager(
                    index = index,
                    count = deck.size,
                    canGoPrevious = canGoPrevious,
                    canGoNext = canGoNext,
                    emphasizeNext = revealed,
                    onPrevious = { goTo(index - 1) },
                    onNext = { goTo(index + 1) },
                )
                Text(
                    text = stringResource(scenario.title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (revealed) {
                    val bannerColor = if (correct) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                    val bannerText = if (correct) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                    Surface(
                        color = bannerColor,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = if (correct) {
                                stringResource(Res.string.crossing_result_correct)
                            } else {
                                stringResource(Res.string.crossing_result_wrong)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = bannerText,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(Res.string.crossing_prompt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CrossingStage(
                        scenario = scenario,
                        progress = progress,
                        pickIndex = pickIndex,
                        activeId = activeId,
                        crashIds = crashIds,
                        onVehicleClick = { vehicle ->
                            if (playing || revealed) return@CrossingStage
                            picks = if (vehicle.id in picks) {
                                picks.filter { it != vehicle.id }
                            } else {
                                picks + vehicle.id
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                OrderChips(
                    vehicles = pickedVehicles,
                    enabled = !playing && !revealed,
                    emptyLabel = stringResource(Res.string.crossing_your_order_empty),
                    onRemove = { id -> picks = picks.filter { it != id } },
                )
                if (revealed) {
                    Text(
                        text = stringResource(
                            Res.string.crossing_right_order,
                            crossingOrder.joinToString(" → ") { it.label },
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (correct) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = when {
                            correct -> stringResource(Res.string.crossing_correct, explanation)
                            clash != null -> {
                                val wrong = scenario.vehicles.first { it.id == clash.wrongId }.label
                                val priority = scenario.vehicles.first { it.id == clash.priorityId }.label
                                stringResource(Res.string.crossing_crash, wrong, priority) +
                                    " " + stringResource(Res.string.crossing_wrong, explanation)
                            }
                            else -> explanation
                        },
                        modifier = Modifier
                            .heightIn(max = 120.dp)
                            .verticalScroll(rememberScrollState()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AppOutlinedButton(
                        text = stringResource(Res.string.crossing_clear),
                        onClick = {
                            stopAndReset()
                            picks = emptyList()
                            revealed = false
                        },
                        enabled = !playing && (picks.isNotEmpty() || revealed),
                        modifier = Modifier.weight(1f),
                    )
                    if (revealed) {
                        AppOutlinedButton(
                            text = if (playing) {
                                stringResource(Res.string.crossing_playing)
                            } else {
                                stringResource(Res.string.crossing_check)
                            },
                            onClick = { playAnswer() },
                            enabled = ready && !playing,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        AppButton(
                            text = if (playing) {
                                stringResource(Res.string.crossing_playing)
                            } else {
                                stringResource(Res.string.crossing_check)
                            },
                            onClick = { playAnswer() },
                            enabled = ready && !playing,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionPager(
    index: Int,
    count: Int,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    emphasizeNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        PagerStep(
            icon = Icons.AutoMirrored.Filled.ArrowBackIos,
            description = stringResource(Res.string.crossing_previous),
            enabled = canGoPrevious,
            filled = false,
            onClick = onPrevious,
        )
        Text(
            text = stringResource(Res.string.crossing_progress, index + 1, count),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        PagerStep(
            icon = Icons.AutoMirrored.Filled.ArrowForwardIos,
            description = stringResource(Res.string.crossing_next),
            enabled = canGoNext,
            filled = emphasizeNext,
            onClick = onNext,
        )
    }
}

@Composable
private fun PagerStep(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    filled: Boolean,
    onClick: () -> Unit,
) {
    val container = when {
        filled && enabled -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val tint = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        filled -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = container,
        modifier = Modifier.size(48.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = tint,
            )
        }
    }
}

@Composable
private fun OrderChips(
    vehicles: List<CrossingVehicle>,
    enabled: Boolean,
    emptyLabel: String,
    onRemove: (String) -> Unit,
) {
    if (vehicles.isEmpty()) {
        Text(
            text = emptyLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        vehicles.forEachIndexed { index, vehicle ->
            val label = "${index + 1} ${vehicle.label}"
            Box(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(enabled = enabled) { onRemove(vehicle.id) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

private data class Clash(val wrongId: String, val priorityId: String)

private fun firstClash(picks: List<String>, legal: List<String>): Clash {
    val index = legal.indices.first { step -> step >= picks.size || picks[step] != legal[step] }
    return Clash(
        wrongId = picks.getOrElse(index) { picks.first() },
        priorityId = legal[index],
    )
}

@Composable
private fun crossingExplanation(scenario: CrossingScenario): String {
    val lines = mutableListOf<String>()
    for (note in scenario.notes) {
        val waiting = scenario.vehicles.first { it.id == note.waitingId }.label
        val ahead = scenario.vehicles.first { it.id == note.aheadId }.label
        lines += when (note.cause) {
            YieldCause.BlueSiren -> stringResource(Res.string.crossing_reason_blue, waiting, ahead)
            YieldCause.MainRoad -> stringResource(Res.string.crossing_reason_main, waiting, ahead)
            YieldCause.TrafficLight -> stringResource(Res.string.crossing_reason_light, waiting, ahead)
            YieldCause.LeftTurn -> stringResource(Res.string.crossing_reason_left, waiting, ahead)
            YieldCause.FromTheRight -> stringResource(Res.string.crossing_reason_right, waiting, ahead)
            YieldCause.PermissiveArrow -> stringResource(Res.string.crossing_reason_arrow, waiting, ahead)
        }
    }
    return lines.joinToString(" ")
}

