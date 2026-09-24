package com.drive.license.test.ui.crossing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.components.AppOutlinedButton
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.crossing_back
import drivelicensetest.ui.generated.resources.crossing_clear
import drivelicensetest.ui.generated.resources.crossing_correct
import drivelicensetest.ui.generated.resources.crossing_kind_car
import drivelicensetest.ui.generated.resources.crossing_kind_emergency
import drivelicensetest.ui.generated.resources.crossing_kind_medical
import drivelicensetest.ui.generated.resources.crossing_kind_police
import drivelicensetest.ui.generated.resources.crossing_kind_works
import drivelicensetest.ui.generated.resources.crossing_next
import drivelicensetest.ui.generated.resources.crossing_playing
import drivelicensetest.ui.generated.resources.crossing_previous
import drivelicensetest.ui.generated.resources.crossing_progress
import drivelicensetest.ui.generated.resources.crossing_prompt
import drivelicensetest.ui.generated.resources.crossing_reason_blue
import drivelicensetest.ui.generated.resources.crossing_reason_left
import drivelicensetest.ui.generated.resources.crossing_reason_main
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
    val scope = rememberCoroutineScope()
    var playJob by remember { mutableStateOf<Job?>(null) }

    fun stopAndReset() {
        playJob?.cancel()
        playJob = null
        playing = false
        activeId = null
        progress = emptyMap()
    }

    fun playAnswer() {
        playJob?.cancel()
        playJob = scope.launch {
            playing = true
            revealed = true
            progress = scenario.vehicles.associate { it.id to 0f }
            crossingOrder.forEachIndexed { step, vehicle ->
                activeId = vehicle.id
                val anim = Animatable(0f)
                anim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                ) {
                    progress = progress + (vehicle.id to value)
                }
                if (step < crossingOrder.lastIndex) delay(220)
            }
            activeId = null
            playing = false
        }
    }

    LaunchedEffect(scenario.id) {
        stopAndReset()
        revealed = false
    }

    val pickIndex = picks.withIndex().associate { it.value to it.index + 1 }
    val correct = picks == crossingOrder.map { it.id }
    val kindLabels = mapOf(
        VehicleKind.General to stringResource(Res.string.crossing_kind_car),
        VehicleKind.Medical to stringResource(Res.string.crossing_kind_medical),
        VehicleKind.Police to stringResource(Res.string.crossing_kind_police),
        VehicleKind.RoadWork to stringResource(Res.string.crossing_kind_works),
        VehicleKind.Emergency to stringResource(Res.string.crossing_kind_emergency),
    )
    val explanation = crossingExplanation(scenario)

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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(scenario.title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(
                        Res.string.crossing_progress,
                        index + 1,
                        deck.size,
                    ) + "  ·  " + stringResource(Res.string.crossing_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                CrossingStage(
                    scenario = scenario,
                    progress = progress,
                    pickIndex = pickIndex,
                    activeId = activeId,
                    onVehicleClick = { vehicle ->
                        if (playing || revealed) return@CrossingStage
                        picks = if (vehicle.id in picks) {
                            picks.filter { it != vehicle.id }
                        } else {
                            picks + vehicle.id
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 280.dp, max = 420.dp),
                )
                Text(
                    text = orderLine(
                        emptyLabel = stringResource(Res.string.crossing_your_order_empty),
                        filledLabel = Res.string.crossing_your_order,
                        vehicles = picks.map { id -> crossingOrder.first { it.id == id } },
                        kindLabels = kindLabels,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (revealed) {
                    Text(
                        text = orderLine(
                            emptyLabel = stringResource(Res.string.crossing_your_order_empty),
                            filledLabel = Res.string.crossing_right_order,
                            vehicles = crossingOrder,
                            kindLabels = kindLabels,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (picks.isNotEmpty() && correct) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
                AnimatedVisibility(visible = revealed, enter = fadeIn(tween(300))) {
                    Text(
                        text = if (picks.isEmpty()) {
                            explanation
                        } else if (correct) {
                            stringResource(Res.string.crossing_correct, explanation)
                        } else {
                            stringResource(Res.string.crossing_wrong, explanation)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AppButton(
                        text = if (playing) {
                            stringResource(Res.string.crossing_playing)
                        } else {
                            stringResource(Res.string.crossing_show)
                        },
                        onClick = { playAnswer() },
                        enabled = !playing,
                        modifier = Modifier.weight(1f),
                    )
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
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AppOutlinedButton(
                        text = stringResource(Res.string.crossing_previous),
                        onClick = { if (index > 0) index -= 1 },
                        enabled = index > 0 && !playing,
                        modifier = Modifier.weight(1f),
                    )
                    AppOutlinedButton(
                        text = stringResource(Res.string.crossing_next),
                        onClick = { if (index < deck.lastIndex) index += 1 },
                        enabled = index < deck.lastIndex && !playing,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
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
            YieldCause.LeftTurn -> stringResource(Res.string.crossing_reason_left, waiting, ahead)
            YieldCause.FromTheRight -> stringResource(Res.string.crossing_reason_right, waiting, ahead)
        }
    }
    return lines.joinToString(" ")
}

@Composable
private fun orderLine(
    emptyLabel: String,
    filledLabel: org.jetbrains.compose.resources.StringResource,
    vehicles: List<CrossingVehicle>,
    kindLabels: Map<VehicleKind, String>,
): String {
    if (vehicles.isEmpty()) return emptyLabel
    val body = vehicles.joinToString(" → ") { "${it.label} ${kindLabels.getValue(it.kind)}" }
    return stringResource(filledLabel, body)
}
