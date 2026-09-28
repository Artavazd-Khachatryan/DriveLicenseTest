package com.drive.license.test.ui.crossing

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot

/** Position on a 0..1 board (y grows downward) plus a clockwise heading, 0 = north. */
data class VehiclePose(
    val x: Float,
    val y: Float,
    val headingDegrees: Float,
)

private const val CENTER = 0.5f
private const val LANE = 0.07f
private const val WAIT = 0.32f
private const val EXIT = 0.38f

/**
 * Where [vehicle] sits while [t] runs from 0 (waiting at the give-way line)
 * to 1 (cleared the junction). Straight paths are linear; turns are a quadratic bend.
 */
fun poseAt(vehicle: CrossingVehicle, t: Float): VehiclePose {
    val clamped = t.coerceIn(0f, 1f)
    val start = waitPoint(vehicle.approach)
    val end = exitPoint(vehicle.approach, vehicle.maneuver)
    val control = if (vehicle.maneuver == Maneuver.Straight) {
        Offset((start.x + end.x) / 2f, (start.y + end.y) / 2f)
    } else {
        bendPoint(vehicle.approach, vehicle.maneuver)
    }
    val position = quadratic(start, control, end, clamped)
    val sample = if (clamped < 0.98f) {
        quadratic(start, control, end, clamped + 0.02f) - position
    } else {
        position - quadratic(start, control, end, clamped - 0.02f)
    }
    val travel = headingDegrees(sample.x, sample.y)
    val heading = if (vehicle.facing == Facing.Reverse) travel + 180f else travel
    return VehiclePose(
        x = position.x,
        y = position.y,
        headingDegrees = heading,
    )
}

private fun waitPoint(approach: Approach): Offset = when (approach) {
    Approach.South -> Offset(CENTER + LANE, CENTER + WAIT)
    Approach.North -> Offset(CENTER - LANE, CENTER - WAIT)
    Approach.West -> Offset(CENTER - WAIT, CENTER + LANE)
    Approach.East -> Offset(CENTER + WAIT, CENTER - LANE)
}

private fun exitPoint(approach: Approach, maneuver: Maneuver): Offset = when (maneuver) {
    Maneuver.Straight -> when (approach) {
        Approach.South -> Offset(CENTER + LANE, CENTER - EXIT)
        Approach.North -> Offset(CENTER - LANE, CENTER + EXIT)
        Approach.West -> Offset(CENTER + EXIT, CENTER + LANE)
        Approach.East -> Offset(CENTER - EXIT, CENTER - LANE)
    }
    Maneuver.TurnRight -> when (approach) {
        Approach.South -> Offset(CENTER + EXIT, CENTER + LANE)
        Approach.West -> Offset(CENTER - LANE, CENTER + EXIT)
        Approach.North -> Offset(CENTER - EXIT, CENTER - LANE)
        Approach.East -> Offset(CENTER + LANE, CENTER - EXIT)
    }
    Maneuver.TurnLeft -> when (approach) {
        Approach.South -> Offset(CENTER - EXIT, CENTER - LANE)
        Approach.West -> Offset(CENTER + LANE, CENTER - EXIT)
        Approach.North -> Offset(CENTER + EXIT, CENTER + LANE)
        Approach.East -> Offset(CENTER - LANE, CENTER + EXIT)
    }
}

/** Corner the turn bends around: near for a right turn, far for a left turn. */
private fun bendPoint(approach: Approach, maneuver: Maneuver): Offset = when (maneuver) {
    Maneuver.Straight -> Offset(CENTER, CENTER)
    Maneuver.TurnRight -> when (approach) {
        Approach.South -> Offset(CENTER + LANE, CENTER + LANE)
        Approach.West -> Offset(CENTER - LANE, CENTER + LANE)
        Approach.North -> Offset(CENTER - LANE, CENTER - LANE)
        Approach.East -> Offset(CENTER + LANE, CENTER - LANE)
    }
    Maneuver.TurnLeft -> when (approach) {
        Approach.South -> Offset(CENTER + LANE, CENTER - LANE)
        Approach.North -> Offset(CENTER - LANE, CENTER + LANE)
        Approach.West -> Offset(CENTER + LANE, CENTER + LANE)
        Approach.East -> Offset(CENTER - LANE, CENTER - LANE)
    }
}

private fun quadratic(p0: Offset, p1: Offset, p2: Offset, t: Float): Offset {
    val u = 1f - t
    return p0 * (u * u) + p1 * (2f * u * t) + p2 * (t * t)
}

/** Nose-up artwork rotated clockwise: 0 faces north. */
private fun headingDegrees(dx: Float, dy: Float): Float {
    if (hypot(dx, dy) < 1e-4f) return 0f
    return atan2(dx.toDouble(), (-dy).toDouble()).toFloat() * (180f / PI.toFloat())
}
