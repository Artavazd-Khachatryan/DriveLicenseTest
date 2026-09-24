package com.drive.license.test.ui.crossing

import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.crossing_case_equal
import drivelicensetest.ui.generated.resources.crossing_case_main
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
                    for (axis in listOf(null, RoadAxis.EastWest, RoadAxis.NorthSouth)) {
                        if (added == perSize || addedHere == perArms) break
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
                            title = titleFor(vehicles, axis),
                            explanation = Res.string.crossing_case_equal,
                            vehicles = vehicles,
                            crossingOrder = emptyList(),
                            priorityAxis = axis,
                        )
                        val order = runCatching { resolveCrossing(draft) }.getOrNull() ?: continue
                        val signature = vehicles.joinToString(",") {
                            "${it.approach}-${it.maneuver}-${it.beacon}"
                        } + "|${axis ?: "equal"}"
                        if (!seen.add(signature)) continue
                        val notes = notesFor(order, axis)
                        scenarios += draft.copy(
                            id = "c${scenarios.size + 1}",
                            crossingOrder = order.map { it.id },
                            notes = notes,
                        )
                        added += 1
                        addedHere += 1
                    }
                }
            }
        }
    }
    return scenarios
}

private fun notesFor(order: List<CrossingVehicle>, axis: RoadAxis?): List<YieldNote> {
    return order.drop(1).map { waiting ->
        val ahead = order.takeWhile { it.id != waiting.id }.last { other ->
            mustYield(waiting, other, axis)
        }
        YieldNote(
            waitingId = waiting.id,
            aheadId = ahead.id,
            cause = checkNotNull(yieldCause(waiting, ahead, axis)),
        )
    }
}

private fun titleFor(vehicles: List<CrossingVehicle>, axis: RoadAxis?): StringResource {
    val hasSignal = vehicles.any { it.beacon == Beacon.BlueSiren }
    val hasTurn = vehicles.any { it.maneuver != Maneuver.Straight }
    val features = listOf(hasSignal, axis != null, hasTurn).count { it }
    if (features > 1) return Res.string.crossing_case_mixed
    return when {
        hasSignal -> Res.string.crossing_case_signal
        axis != null -> Res.string.crossing_case_main
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
