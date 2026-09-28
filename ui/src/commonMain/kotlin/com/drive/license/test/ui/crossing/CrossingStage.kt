package com.drive.license.test.ui.crossing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.crossing_kind_car
import drivelicensetest.ui.generated.resources.crossing_kind_emergency
import drivelicensetest.ui.generated.resources.crossing_kind_medical
import drivelicensetest.ui.generated.resources.crossing_kind_police
import drivelicensetest.ui.generated.resources.crossing_kind_works
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private val Asphalt = Color(0xFF3F4A57)
private val LanePaint = Color(0xFFF8FAFC)
private val CenterLine = Color(0xFFFBBF24)
private val Verge = Color(0xFF3F6B45)

/**
 * Top-down junction. [progress] is 0..1 per vehicle id (missing means still waiting).
 * [pickIndex] is the 1-based tap order while the user is answering; null hides the badge.
 */
@Composable
fun CrossingStage(
    scenario: CrossingScenario,
    progress: Map<String, Float>,
    pickIndex: Map<String, Int>,
    activeId: String?,
    crashIds: Set<String> = emptySet(),
    onVehicleClick: (CrossingVehicle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    BoxWithConstraints(
        modifier = modifier.background(scheme.surfaceContainer),
        contentAlignment = Alignment.Center,
    ) {
        val side = minOf(maxWidth, maxHeight)
        val density = LocalDensity.current
        val sidePx = with(density) { side.toPx() }

        Box(modifier = Modifier.size(side)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val road = 0.30f
                drawRect(Verge)
                drawRect(
                    color = Asphalt,
                    topLeft = androidx.compose.ui.geometry.Offset(0f, h * (0.5f - road / 2f)),
                    size = androidx.compose.ui.geometry.Size(w, h * road),
                )
                drawRect(
                    color = Asphalt,
                    topLeft = androidx.compose.ui.geometry.Offset(w * (0.5f - road / 2f), 0f),
                    size = androidx.compose.ui.geometry.Size(w * road, h),
                )
                val dash = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), 0f)
                drawLine(
                    color = CenterLine,
                    start = androidx.compose.ui.geometry.Offset(w / 2f, 0f),
                    end = androidx.compose.ui.geometry.Offset(w / 2f, h * 0.35f),
                    strokeWidth = 4f,
                    pathEffect = dash,
                )
                drawLine(
                    color = CenterLine,
                    start = androidx.compose.ui.geometry.Offset(w / 2f, h * 0.65f),
                    end = androidx.compose.ui.geometry.Offset(w / 2f, h),
                    strokeWidth = 4f,
                    pathEffect = dash,
                )
                drawLine(
                    color = CenterLine,
                    start = androidx.compose.ui.geometry.Offset(0f, h / 2f),
                    end = androidx.compose.ui.geometry.Offset(w * 0.35f, h / 2f),
                    strokeWidth = 4f,
                    pathEffect = dash,
                )
                drawLine(
                    color = CenterLine,
                    start = androidx.compose.ui.geometry.Offset(w * 0.65f, h / 2f),
                    end = androidx.compose.ui.geometry.Offset(w, h / 2f),
                    strokeWidth = 4f,
                    pathEffect = dash,
                )
                val sign = when (val control = scenario.control) {
                    is JunctionControl.Sign -> control
                    is JunctionControl.Lights -> control.ignoredSign
                    JunctionControl.Equal -> null
                }
                if (sign != null) {
                    val arms = signArms(sign)
                    fun thick(approach: Approach) {
                        val (x1, y1, x2, y2) = armLine(approach)
                        drawLine(
                            CenterLine,
                            androidx.compose.ui.geometry.Offset(w * x1, h * y1),
                            androidx.compose.ui.geometry.Offset(w * x2, h * y2),
                            strokeWidth = 8f,
                        )
                    }
                    arms.forEach(::thick)
                    if (sign.bend != Bend.Straight) {
                        val from = armInner(sign.from)
                        val into = armInner(otherArm(sign.from, sign.bend))
                        drawLine(
                            CenterLine,
                            androidx.compose.ui.geometry.Offset(w * from.first, h * from.second),
                            androidx.compose.ui.geometry.Offset(w * into.first, h * into.second),
                            strokeWidth = 8f,
                        )
                    }
                }
                if (scenario.control is JunctionControl.Lights) {
                    val lights = scenario.control
                    fun lamp(approach: Approach, maneuver: Maneuver, x: Float, y: Float) {
                        val probe = CrossingVehicle(
                            id = "lamp",
                            label = "",
                            kind = VehicleKind.General,
                            approach = approach,
                            maneuver = maneuver,
                        )
                        drawCircle(
                            color = if (movementIsGreen(probe, lights)) Color(0xFF16A34A) else Color(0xFFDC2626),
                            radius = 7f,
                            center = androidx.compose.ui.geometry.Offset(w * x, h * y),
                        )
                    }
                    for (approach in Approach.entries) {
                        for (maneuver in Maneuver.entries) {
                            val (x, y) = lampSpot(approach, maneuver)
                            lamp(approach, maneuver, x, y)
                        }
                    }
                }
                val stop = 0.66f
                val lane = 0.07f
                fun bar(x1: Float, y1: Float, x2: Float, y2: Float) {
                    drawLine(
                        color = LanePaint,
                        start = androidx.compose.ui.geometry.Offset(w * x1, h * y1),
                        end = androidx.compose.ui.geometry.Offset(w * x2, h * y2),
                        strokeWidth = 5f,
                    )
                }
                bar(0.5f + 0.02f, stop, 0.5f + lane + 0.06f, stop)
                bar(0.5f - lane - 0.06f, 1f - stop, 0.5f - 0.02f, 1f - stop)
                bar(1f - stop, 0.5f - lane - 0.06f, 1f - stop, 0.5f - 0.02f)
                bar(stop, 0.5f + 0.02f, stop, 0.5f + lane + 0.06f)
            }

            scenario.vehicles.forEach { vehicle ->
                val pose = poseAt(vehicle, progress[vehicle.id] ?: 0f)
                val carWidth = side * 0.16f
                val carHeight = side * 0.22f
                val left = pose.x * sidePx - with(density) { carWidth.toPx() } / 2f
                val top = pose.y * sidePx - with(density) { carHeight.toPx() } / 2f
                val picked = pickIndex[vehicle.id]
                val moving = vehicle.id == activeId
                val crashed = vehicle.id in crashIds
                val description = vehicle.label + ", " + kindLabel(vehicle.kind)
                Box(
                    modifier = Modifier
                        .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                        .size(carWidth, carHeight)
                        .graphicsLayer {
                            rotationZ = pose.headingDegrees
                            scaleX = if (crashed) 0.92f else if (moving) 1.06f else 1f
                            scaleY = if (crashed) 0.92f else if (moving) 1.06f else 1f
                        }
                        .clickable { onVehicleClick(vehicle) }
                        .semantics {
                            role = Role.Button
                            contentDescription = description
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    CrossingCarArt(kind = vehicle.kind)
                    if (crashed) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x66DC2626), RoundedCornerShape(12.dp)),
                        )
                    }
                    if (moving || picked != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    drawRoundRect(
                                        color = if (moving) scheme.tertiary else scheme.primary,
                                        style = Stroke(width = 6f),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.28f),
                                    )
                                },
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .graphicsLayer { rotationZ = -pose.headingDegrees },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (picked != null) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(scheme.primary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = picked.toString(),
                                    color = scheme.onPrimary,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Text(
                            text = vehicle.label,
                            modifier = Modifier
                                .background(scheme.surface, CircleShape)
                                .padding(horizontal = 4.dp),
                            color = scheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

private data class Segment(val x1: Float, val y1: Float, val x2: Float, val y2: Float)

private fun armLine(approach: Approach): Segment = when (approach) {
    Approach.North -> Segment(0.5f, 0f, 0.5f, 0.35f)
    Approach.South -> Segment(0.5f, 0.65f, 0.5f, 1f)
    Approach.East -> Segment(0.65f, 0.5f, 1f, 0.5f)
    Approach.West -> Segment(0f, 0.5f, 0.35f, 0.5f)
}

/** Three lamps per approach: left, straight, right, as the driver waiting there sees them. */
private fun lampSpot(approach: Approach, maneuver: Maneuver): Pair<Float, Float> {
    val slot = when (maneuver) {
        Maneuver.TurnLeft -> 0
        Maneuver.Straight -> 1
        Maneuver.TurnRight -> 2
    }
    val step = 0.045f
    return when (approach) {
        Approach.North -> (0.66f) to (0.16f + slot * step)
        Approach.South -> (0.34f) to (0.84f - slot * step)
        Approach.East -> (0.84f - slot * step) to 0.66f
        Approach.West -> (0.16f + slot * step) to 0.34f
    }
}

private fun armInner(approach: Approach): Pair<Float, Float> = when (approach) {
    Approach.North -> 0.5f to 0.35f
    Approach.South -> 0.5f to 0.65f
    Approach.East -> 0.65f to 0.5f
    Approach.West -> 0.35f to 0.5f
}

@Composable
private fun kindLabel(kind: VehicleKind): String = when (kind) {
    VehicleKind.General -> stringResource(Res.string.crossing_kind_car)
    VehicleKind.Medical -> stringResource(Res.string.crossing_kind_medical)
    VehicleKind.Police -> stringResource(Res.string.crossing_kind_police)
    VehicleKind.RoadWork -> stringResource(Res.string.crossing_kind_works)
    VehicleKind.Emergency -> stringResource(Res.string.crossing_kind_emergency)
}
