package com.drive.license.test.ui.crossing

import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.crossing_case_arrow
import drivelicensetest.ui.generated.resources.crossing_case_equal
import drivelicensetest.ui.generated.resources.crossing_case_lights
import drivelicensetest.ui.generated.resources.crossing_case_main_left
import drivelicensetest.ui.generated.resources.crossing_case_main_right
import drivelicensetest.ui.generated.resources.crossing_case_main_straight
import drivelicensetest.ui.generated.resources.crossing_case_mixed
import drivelicensetest.ui.generated.resources.crossing_case_signal
import drivelicensetest.ui.generated.resources.crossing_case_turns
import org.jetbrains.compose.resources.StringResource

private val labels = listOf("Ա", "Բ", "Գ", "Դ")
private const val perSize = 40

/**
 * Builds a varied deck of junctions the rules settle to one order.
 * Each item stores that order. Paint (car, ambulance, police) does not change it.
 * Tied junctions, including a full four-way where everyone has a car on the right, are skipped.
 */
fun generateCrossingScenarios(): List<CrossingScenario> {
    val scenarios = mutableListOf<CrossingScenario>()
    val seen = mutableSetOf<String>()
    for (count in 2..4) {
        var added = 0
        val perArms = if (count == 4) perSize else 8
        for (arms in combinations(Approach.entries, count)) {
            if (added == perSize) break
            var addedHere = 0
            for (moves in products(Maneuver.entries, count)) {
                if (added == perSize || addedHere == perArms) break
                for (beacons in beaconPlans(count)) {
                    if (added == perSize || addedHere == perArms) break
                    for (control in straightControls()) {
                        if (added == perSize || addedHere == perArms) break
                        if (accept(scenarios, seen, arms, moves, beacons, control)) {
                            added += 1
                            addedHere += 1
                        }
                    }
                }
            }
        }
    }
    addBendScenarios(scenarios, seen)
    return scenarios
}

private fun straightControls(): List<JunctionControl> = listOf(
    JunctionControl.Equal,
    JunctionControl.Sign(Approach.South, Bend.Straight),
    JunctionControl.Sign(Approach.West, Bend.Straight),
)

private fun accept(
    scenarios: MutableList<CrossingScenario>,
    seen: MutableSet<String>,
    arms: List<Approach>,
    moves: List<Maneuver>,
    beacons: List<Beacon>,
    control: JunctionControl,
): Boolean {
    val vehicles = arms.indices.map { index ->
        CrossingVehicle(
            id = "v$index",
            label = labels[index],
            kind = kindFor(beacons[index], index),
            approach = arms[index],
            maneuver = moves[index],
            beacon = beacons[index],
        )
    }
    if (control is JunctionControl.Lights) return false
    // Two cars on equal roads: the straight and turning paths are hard to tell apart.
    if (vehicles.size == 2 && control !is JunctionControl.Sign) return false
    if (vehicles.size == 2 && vehicles.all { it.maneuver == Maneuver.Straight }) return false
    val draft = CrossingScenario(
        id = "draft",
        title = titleFor(vehicles, control),
        explanation = Res.string.crossing_case_equal,
        vehicles = vehicles,
        crossingOrder = emptyList(),
        control = control,
    )
    val order = runCatching { resolveCrossing(draft) }.getOrNull() ?: return false
    val signature = vehicles.joinToString(",") {
        "${it.approach}-${it.maneuver}-${it.beacon}"
    } + "|${controlKey(control)}"
    if (!seen.add(signature)) return false
    scenarios += draft.copy(
        id = "c${scenarios.size + 1}",
        crossingOrder = order.map { it.id },
        notes = notesFor(order, control),
    )
    return true
}

/** One sample for every main-road bend. Traffic lights stay out of the deck. */
private fun addBendScenarios(
    scenarios: MutableList<CrossingScenario>,
    seen: MutableSet<String>,
) {
    val none = listOf(Beacon.None, Beacon.None)
    for (from in Approach.entries) {
        val side = Approach.entries.first { it != from && it != otherArm(from, Bend.Straight) }
        for (bend in Bend.entries) {
            val along = when (bend) {
                Bend.Left -> Maneuver.TurnLeft
                Bend.Straight -> Maneuver.Straight
                Bend.Right -> Maneuver.TurnRight
            }
            val sideMove = if (along == Maneuver.Straight) Maneuver.TurnLeft else Maneuver.Straight
            accept(
                scenarios, seen,
                listOf(from, side),
                listOf(along, sideMove),
                none,
                JunctionControl.Sign(from, bend),
            )
        }
    }
}

private fun controlKey(control: JunctionControl): String = when (control) {
    JunctionControl.Equal -> "equal"
    is JunctionControl.Sign -> "sign-${control.from}-${control.bend}"
    is JunctionControl.Lights ->
        "lights-${control.green}-${control.arrow?.approach}-${control.arrow?.maneuver}-${control.ignoredSign?.from}"
}

private fun notesFor(order: List<CrossingVehicle>, control: JunctionControl): List<YieldNote> {
    return order.drop(1).map { waiting ->
        val ahead = order.takeWhile { it.id != waiting.id }.last { other ->
            mustYield(waiting, other, control)
        }
        YieldNote(
            waitingId = waiting.id,
            aheadId = ahead.id,
            cause = checkNotNull(yieldCause(waiting, ahead, control)),
        )
    }
}

private fun titleFor(vehicles: List<CrossingVehicle>, control: JunctionControl): StringResource {
    if (control is JunctionControl.Lights && control.arrow != null) return Res.string.crossing_case_arrow
    if (control is JunctionControl.Lights) return Res.string.crossing_case_lights
    if (control is JunctionControl.Sign) {
        return when (control.bend) {
            Bend.Left -> Res.string.crossing_case_main_left
            Bend.Straight -> Res.string.crossing_case_main_straight
            Bend.Right -> Res.string.crossing_case_main_right
        }
    }
    val hasSignal = vehicles.any { it.beacon == Beacon.BlueSiren }
    val hasTurn = vehicles.any { it.maneuver != Maneuver.Straight }
    if (hasSignal && hasTurn) return Res.string.crossing_case_mixed
    return when {
        hasSignal -> Res.string.crossing_case_signal
        hasTurn -> Res.string.crossing_case_turns
        else -> Res.string.crossing_case_equal
    }
}

private fun kindFor(beacon: Beacon, index: Int): VehicleKind = when (beacon) {
    Beacon.Orange -> VehicleKind.RoadWork
    Beacon.BlueSiren -> listOf(
        VehicleKind.Medical,
        VehicleKind.Police,
        VehicleKind.Emergency,
    )[index % 3]
    Beacon.None -> VehicleKind.General
}

private fun beaconPlans(count: Int): List<List<Beacon>> {
    val plans = mutableListOf<List<Beacon>>()
    plans += List(count) { Beacon.None }
    for (index in 0 until count) {
        plans += List(count) { if (it == index) Beacon.BlueSiren else Beacon.None }
        plans += List(count) { if (it == index) Beacon.Orange else Beacon.None }
    }
    if (count >= 2) {
        for (first in 0 until count) {
            for (second in first + 1 until count) {
                plans += List(count) {
                    when (it) {
                        first -> Beacon.BlueSiren
                        second -> Beacon.Orange
                        else -> Beacon.None
                    }
                }
                plans += List(count) {
                    when (it) {
                        first, second -> Beacon.BlueSiren
                        else -> Beacon.None
                    }
                }
            }
        }
    }
    return plans
}

private fun <T> combinations(items: List<T>, count: Int): List<List<T>> {
    val out = mutableListOf<List<T>>()
    fun walk(start: Int, prefix: List<T>) {
        if (prefix.size == count) {
            out += prefix
            return
        }
        for (index in start..items.lastIndex) walk(index + 1, prefix + items[index])
    }
    walk(0, emptyList())
    return out
}

private fun <T> products(items: List<T>, count: Int): List<List<T>> {
    val out = mutableListOf<List<T>>()
    fun walk(prefix: List<T>) {
        if (prefix.size == count) {
            out += prefix
            return
        }
        for (item in items) walk(prefix + item)
    }
    walk(emptyList())
    return out
}
