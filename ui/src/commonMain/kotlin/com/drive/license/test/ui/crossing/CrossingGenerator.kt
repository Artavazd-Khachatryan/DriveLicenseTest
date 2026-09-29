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
    addBendAndLightScenarios(scenarios, seen)
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

/** One sample for every bend, and for lights both with and without a sign behind them. */
private fun addBendAndLightScenarios(
    scenarios: MutableList<CrossingScenario>,
    seen: MutableSet<String>,
) {
    val none = listOf(Beacon.None, Beacon.None)
    val straight = listOf(Maneuver.Straight, Maneuver.Straight)
    for (from in Approach.entries) {
        val side = Approach.entries.first { it != from && it != otherArm(from, Bend.Straight) }
        for (bend in Bend.entries) {
            val along = when (bend) {
                Bend.Left -> Maneuver.TurnLeft
                Bend.Straight -> Maneuver.Straight
                Bend.Right -> Maneuver.TurnRight
            }
            accept(
                scenarios, seen,
                listOf(from, side),
                listOf(along, Maneuver.Straight),
                none,
                JunctionControl.Sign(from, bend),
            )
        }
    }
    for (phase in SignalPhase.entries) {
        val greenArm = if (phase == SignalPhase.NorthSouth) Approach.South else Approach.West
        val redArm = if (phase == SignalPhase.NorthSouth) Approach.East else Approach.South
        accept(
            scenarios, seen,
            listOf(greenArm, redArm),
            straight,
            none,
            JunctionControl.Lights(phase),
        )
        val sign = JunctionControl.Sign(
            if (phase == SignalPhase.NorthSouth) Approach.West else Approach.South,
            Bend.Straight,
        )
        accept(
            scenarios, seen,
            listOf(greenArm, redArm),
            listOf(Maneuver.Straight, Maneuver.TurnLeft),
            none,
            JunctionControl.Lights(phase, ignoredSign = sign),
        )
    }
    // §91: red main light plus a green arrow. The turn yields to the green cross street.
    accept(
        scenarios, seen,
        listOf(Approach.South, Approach.West),
        listOf(Maneuver.TurnLeft, Maneuver.Straight),
        none,
        JunctionControl.Lights(
            green = SignalPhase.EastWest,
            arrow = ArrowSignal(Approach.South, Maneuver.TurnLeft),
        ),
    )
    accept(
        scenarios, seen,
        listOf(Approach.East, Approach.South),
        listOf(Maneuver.TurnRight, Maneuver.Straight),
        none,
        JunctionControl.Lights(
            green = SignalPhase.NorthSouth,
            arrow = ArrowSignal(Approach.East, Maneuver.TurnRight),
        ),
    )
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
