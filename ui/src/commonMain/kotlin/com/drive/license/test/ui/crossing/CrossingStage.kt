package com.drive.license.test.ui.crossing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
                if (scenario.priorityAxis == RoadAxis.EastWest) {
                    drawLine(CenterLine, androidx.compose.ui.geometry.Offset(0f, h / 2f), androidx.compose.ui.geometry.Offset(w * 0.35f, h / 2f), strokeWidth = 8f)
                    drawLine(CenterLine, androidx.compose.ui.geometry.Offset(w * 0.65f, h / 2f), androidx.compose.ui.geometry.Offset(w, h / 2f), strokeWidth = 8f)
                }
                if (scenario.priorityAxis == RoadAxis.NorthSouth) {
                    drawLine(CenterLine, androidx.compose.ui.geometry.Offset(w / 2f, 0f), androidx.compose.ui.geometry.Offset(w / 2f, h * 0.35f), strokeWidth = 8f)
                    drawLine(CenterLine, androidx.compose.ui.geometry.Offset(w / 2f, h * 0.65f), androidx.compose.ui.geometry.Offset(w / 2f, h), strokeWidth = 8f)
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
                val description = vehicle.label + ", " + kindLabel(vehicle.kind)
                Box(
                    modifier = Modifier
                        .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                        .size(carWidth, carHeight)
                        .graphicsLayer {
                            rotationZ = pose.headingDegrees
                            scaleX = if (moving) 1.06f else 1f
                            scaleY = if (moving) 1.06f else 1f
                        }
                        .clickable { onVehicleClick(vehicle) }
                        .semantics {
                            role = Role.Button
                            contentDescription = description
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    CrossingCarArt(kind = vehicle.kind)
                    if (moving) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    drawRoundRect(
                                        color = scheme.tertiary,
                                        style = Stroke(width = 6f),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.28f),
                                    )
                                },
                        )
                    }
                    Text(
                        text = vehicle.label,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .graphicsLayer { rotationZ = -pose.headingDegrees }
                            .background(scheme.surface, CircleShape)
                            .padding(horizontal = 4.dp),
                        color = scheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                if (picked != null) {
                    val badge = 22.dp
                    val badgePx = with(density) { badge.toPx() }
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (pose.x * sidePx + with(density) { carWidth.toPx() } * 0.28f).roundToInt(),
                                    (pose.y * sidePx - with(density) { carHeight.toPx() } * 0.42f - badgePx).roundToInt(),
                                )
                            }
                            .size(badge)
                            .clip(CircleShape)
                            .background(scheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = picked.toString(),
                            color = scheme.onPrimary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun kindLabel(kind: VehicleKind): String = when (kind) {
    VehicleKind.General -> stringResource(Res.string.crossing_kind_car)
    VehicleKind.Medical -> stringResource(Res.string.crossing_kind_medical)
    VehicleKind.Police -> stringResource(Res.string.crossing_kind_police)
    VehicleKind.RoadWork -> stringResource(Res.string.crossing_kind_works)
    VehicleKind.Emergency -> stringResource(Res.string.crossing_kind_emergency)
}
