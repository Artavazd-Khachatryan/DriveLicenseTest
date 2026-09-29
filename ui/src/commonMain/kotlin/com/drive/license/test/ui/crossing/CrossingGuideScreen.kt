package com.drive.license.test.ui.crossing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drive.license.test.ui.components.AppButton
import com.drive.license.test.ui.components.AppCard
import com.drive.license.test.ui.components.AppScaffold
import com.drive.license.test.ui.util.AdaptiveContentContainer
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.crossing_back
import drivelicensetest.ui.generated.resources.crossing_guide_arrow_body
import drivelicensetest.ui.generated.resources.crossing_guide_arrow_title
import drivelicensetest.ui.generated.resources.crossing_guide_begin
import drivelicensetest.ui.generated.resources.crossing_guide_bend_body
import drivelicensetest.ui.generated.resources.crossing_guide_bend_left_title
import drivelicensetest.ui.generated.resources.crossing_guide_bend_right_title
import drivelicensetest.ui.generated.resources.crossing_guide_car
import drivelicensetest.ui.generated.resources.crossing_guide_cars_title
import drivelicensetest.ui.generated.resources.crossing_guide_emergency
import drivelicensetest.ui.generated.resources.crossing_guide_equal_body
import drivelicensetest.ui.generated.resources.crossing_guide_equal_title
import drivelicensetest.ui.generated.resources.crossing_guide_lights_body
import drivelicensetest.ui.generated.resources.crossing_guide_lights_title
import drivelicensetest.ui.generated.resources.crossing_guide_main_body
import drivelicensetest.ui.generated.resources.crossing_guide_main_title
import drivelicensetest.ui.generated.resources.crossing_guide_medical
import drivelicensetest.ui.generated.resources.crossing_guide_nose
import drivelicensetest.ui.generated.resources.crossing_guide_police
import drivelicensetest.ui.generated.resources.crossing_guide_purpose
import drivelicensetest.ui.generated.resources.crossing_guide_purpose_title
import drivelicensetest.ui.generated.resources.crossing_guide_signs_title
import drivelicensetest.ui.generated.resources.crossing_guide_step_check
import drivelicensetest.ui.generated.resources.crossing_guide_step_move
import drivelicensetest.ui.generated.resources.crossing_guide_step_pick
import drivelicensetest.ui.generated.resources.crossing_guide_step_undo
import drivelicensetest.ui.generated.resources.crossing_guide_step_watch
import drivelicensetest.ui.generated.resources.crossing_guide_stop_body
import drivelicensetest.ui.generated.resources.crossing_guide_title
import drivelicensetest.ui.generated.resources.crossing_guide_use_title
import drivelicensetest.ui.generated.resources.crossing_guide_works
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CrossingGuideScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onBack() }
    val steps = listOf(
        stringResource(Res.string.crossing_guide_step_pick),
        stringResource(Res.string.crossing_guide_step_undo),
        stringResource(Res.string.crossing_guide_step_check),
        stringResource(Res.string.crossing_guide_step_watch),
        stringResource(Res.string.crossing_guide_step_move),
    )
    AppScaffold(
        topBarTitle = stringResource(Res.string.crossing_guide_title),
        navigationIcon = {
            AppBackButton(onBack)
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
                GuideSection(title = stringResource(Res.string.crossing_guide_purpose_title)) {
                    GuideBody(stringResource(Res.string.crossing_guide_purpose))
                }
                GuideSection(title = stringResource(Res.string.crossing_guide_use_title)) {
                    LessonBoard(
                        scenario = tapLesson,
                        pickIndex = mapOf("a" to 1),
                    )
                    steps.forEachIndexed { index, step ->
                        StepRow(number = index + 1, text = step)
                    }
                }
                Text(
                    text = stringResource(Res.string.crossing_guide_signs_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                PatternCard(
                    title = stringResource(Res.string.crossing_guide_equal_title),
                    meaning = stringResource(Res.string.crossing_guide_equal_body),
                    scenario = equalRoads,
                )
                PatternCard(
                    title = stringResource(Res.string.crossing_guide_main_title),
                    meaning = stringResource(Res.string.crossing_guide_main_body),
                    scenario = mainStraight,
                )
                PatternCard(
                    title = stringResource(Res.string.crossing_guide_bend_left_title),
                    meaning = stringResource(Res.string.crossing_guide_bend_body),
                    scenario = mainBendsLeft,
                )
                PatternCard(
                    title = stringResource(Res.string.crossing_guide_bend_right_title),
                    meaning = stringResource(Res.string.crossing_guide_bend_body),
                    scenario = mainBendsRight,
                )
                GuideBody(stringResource(Res.string.crossing_guide_stop_body))
                PatternCard(
                    title = stringResource(Res.string.crossing_guide_lights_title),
                    meaning = stringResource(Res.string.crossing_guide_lights_body),
                    scenario = lightsReplaceSign,
                )
                PatternCard(
                    title = stringResource(Res.string.crossing_guide_arrow_title),
                    meaning = stringResource(Res.string.crossing_guide_arrow_body),
                    scenario = permissiveArrow,
                )
                GuideSection(title = stringResource(Res.string.crossing_guide_cars_title)) {
                    VehicleRow(VehicleKind.General, stringResource(Res.string.crossing_guide_car))
                    VehicleRow(VehicleKind.Medical, stringResource(Res.string.crossing_guide_medical))
                    VehicleRow(VehicleKind.Police, stringResource(Res.string.crossing_guide_police))
                    VehicleRow(VehicleKind.Emergency, stringResource(Res.string.crossing_guide_emergency))
                    VehicleRow(VehicleKind.RoadWork, stringResource(Res.string.crossing_guide_works))
                    GuideBody(stringResource(Res.string.crossing_guide_nose))
                }
                AppButton(
                    text = stringResource(Res.string.crossing_guide_begin),
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun AppBackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.crossing_back),
        )
    }
}

@Composable
private fun PatternCard(
    title: String,
    meaning: String,
    scenario: CrossingScenario,
) {
    AppCard(modifier = Modifier.fillMaxWidth(), elevation = 0.dp) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            LessonBoard(scenario)
            GuideBody(meaning)
        }
    }
}

@Composable
private fun LessonBoard(
    scenario: CrossingScenario,
    pickIndex: Map<String, Int> = emptyMap(),
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(MaterialTheme.shapes.medium),
    ) {
        CrossingStage(
            scenario = scenario,
            progress = emptyMap(),
            pickIndex = pickIndex,
            activeId = null,
            onVehicleClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun GuideSection(
    title: String,
    content: @Composable () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth(), elevation = 0.dp) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            content()
        }
    }
}

@Composable
private fun GuideBody(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun StepRow(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semanticsMerged(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        GuideBody(text, Modifier.weight(1f))
    }
}

@Composable
private fun VehicleRow(kind: VehicleKind, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semanticsMerged(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(width = 40.dp, height = 56.dp)) {
            CrossingCarArt(kind = kind)
        }
        GuideBody(text, Modifier.weight(1f))
    }
}

private fun Modifier.semanticsMerged(): Modifier =
    this.then(Modifier.semantics(mergeDescendants = true) {})

private fun lesson(
    id: String,
    title: StringResource,
    vehicles: List<CrossingVehicle>,
    control: JunctionControl,
) = CrossingScenario(
    id = id,
    title = title,
    explanation = title,
    vehicles = vehicles,
    crossingOrder = vehicles.map { it.id },
    control = control,
)

private fun car(
    id: String,
    label: String,
    approach: Approach,
    maneuver: Maneuver = Maneuver.Straight,
    kind: VehicleKind = VehicleKind.General,
    beacon: Beacon = Beacon.None,
) = CrossingVehicle(id, label, kind, approach, maneuver, beacon)

private val tapLesson = lesson(
    id = "learn-tap",
    title = Res.string.crossing_guide_use_title,
    vehicles = listOf(car("a", "Ա", Approach.South)),
    control = JunctionControl.Equal,
)

private val equalRoads = lesson(
    id = "learn-equal",
    title = Res.string.crossing_guide_equal_title,
    vehicles = listOf(
        car("a", "Ա", Approach.South),
        car("b", "Բ", Approach.East),
    ),
    control = JunctionControl.Equal,
)

private val mainStraight = lesson(
    id = "learn-main",
    title = Res.string.crossing_guide_main_title,
    vehicles = listOf(
        car("a", "Ա", Approach.West),
        car("b", "Բ", Approach.South),
    ),
    control = JunctionControl.Sign(Approach.West, Bend.Straight),
)

private val mainBendsLeft = lesson(
    id = "learn-left",
    title = Res.string.crossing_guide_bend_left_title,
    vehicles = listOf(car("a", "Ա", Approach.South, Maneuver.TurnLeft)),
    control = JunctionControl.Sign(Approach.South, Bend.Left),
)

private val mainBendsRight = lesson(
    id = "learn-right",
    title = Res.string.crossing_guide_bend_right_title,
    vehicles = listOf(car("a", "Ա", Approach.South, Maneuver.TurnRight)),
    control = JunctionControl.Sign(Approach.South, Bend.Right),
)

private val lightsReplaceSign = lesson(
    id = "learn-lights",
    title = Res.string.crossing_guide_lights_title,
    vehicles = listOf(
        car("a", "Ա", Approach.West),
        car("b", "Բ", Approach.South),
    ),
    control = JunctionControl.Lights(
        green = SignalPhase.EastWest,
        ignoredSign = JunctionControl.Sign(Approach.South, Bend.Straight),
    ),
)

private val permissiveArrow = lesson(
    id = "learn-arrow",
    title = Res.string.crossing_guide_arrow_title,
    vehicles = listOf(
        car("a", "Ա", Approach.South, Maneuver.TurnLeft),
        car("b", "Բ", Approach.West),
    ),
    control = JunctionControl.Lights(
        green = SignalPhase.EastWest,
        arrow = ArrowSignal(Approach.South, Maneuver.TurnLeft),
    ),
)
