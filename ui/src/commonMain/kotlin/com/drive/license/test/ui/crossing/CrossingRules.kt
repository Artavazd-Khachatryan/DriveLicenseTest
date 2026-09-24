package com.drive.license.test.ui.crossing

/**
 * Order at an uncontrolled junction, from the RA traffic rules:
 * - §28 blue beacon and siren: everyone else gives way
 * - §32 orange beacon: no priority
 * - §96 secondary road gives way to the main road, whatever the direction
 * - §98 equal roads: give way to the vehicle on the right
 * - §99 left turn gives way to oncoming traffic going straight or turning right
 *
 * Two vehicles with the same beacon rank use the road rules between them.
 * The result is a single order. A situation the rules leave tied is rejected.
 */
fun resolveCrossing(scenario: CrossingScenario): List<CrossingVehicle> {
    val remaining = scenario.vehicles.toMutableList()
    val order = mutableListOf<CrossingVehicle>()
    while (remaining.isNotEmpty()) {
        val ready = remaining.filter { vehicle ->
            remaining.none { other -> mustYield(vehicle, other, scenario.priorityAxis) }
        }
        check(ready.size == 1) {
            "Rules do not fix a single next vehicle among ${ready.map { it.label }}"
        }
        val next = ready.single()
        order += next
        remaining.remove(next)
    }
    return order
}

fun mustYield(
    vehicle: CrossingVehicle,
    other: CrossingVehicle,
    priorityAxis: RoadAxis?,
): Boolean {
    if (vehicle.id == other.id) return false
    val vehiclePrivileged = vehicle.beacon == Beacon.BlueSiren
    val otherPrivileged = other.beacon == Beacon.BlueSiren
    if (vehiclePrivileged != otherPrivileged) return otherPrivileged

    val vehicleOnMain = onPriorityRoad(vehicle, priorityAxis)
    val otherOnMain = onPriorityRoad(other, priorityAxis)
    if (vehicleOnMain != otherOnMain) return otherOnMain

    if (isOncoming(vehicle.approach, other.approach)) {
        val vehicleTurnsLeft = vehicle.maneuver == Maneuver.TurnLeft
        val otherTurnsLeft = other.maneuver == Maneuver.TurnLeft
        if (vehicleTurnsLeft != otherTurnsLeft) return vehicleTurnsLeft
    }
    return isFromTheRight(other.approach, vehicle.approach)
}

/** Why [vehicle] waits for [other], or null when it does not. */
fun yieldCause(
    vehicle: CrossingVehicle,
    other: CrossingVehicle,
    priorityAxis: RoadAxis?,
): YieldCause? {
    if (!mustYield(vehicle, other, priorityAxis)) return null
    val vehiclePrivileged = vehicle.beacon == Beacon.BlueSiren
    val otherPrivileged = other.beacon == Beacon.BlueSiren
    if (vehiclePrivileged != otherPrivileged) return YieldCause.BlueSiren
    val vehicleOnMain = onPriorityRoad(vehicle, priorityAxis)
    val otherOnMain = onPriorityRoad(other, priorityAxis)
    if (vehicleOnMain != otherOnMain) return YieldCause.MainRoad
    if (isOncoming(vehicle.approach, other.approach) &&
        vehicle.maneuver == Maneuver.TurnLeft &&
        other.maneuver != Maneuver.TurnLeft
    ) {
        return YieldCause.LeftTurn
    }
    return YieldCause.FromTheRight
}

private fun onPriorityRoad(vehicle: CrossingVehicle, axis: RoadAxis?): Boolean {
    if (axis == null) return true
    return approachAxis(vehicle.approach) == axis
}

private fun approachAxis(approach: Approach): RoadAxis = when (approach) {
    Approach.North, Approach.South -> RoadAxis.NorthSouth
    Approach.East, Approach.West -> RoadAxis.EastWest
}

private fun isOncoming(approach: Approach, other: Approach): Boolean = when (approach) {
    Approach.North -> other == Approach.South
    Approach.South -> other == Approach.North
    Approach.East -> other == Approach.West
    Approach.West -> other == Approach.East
}

/** True when [other] is the arm on the driver's right as they face the junction. */
private fun isFromTheRight(other: Approach, self: Approach): Boolean = when (self) {
    Approach.South -> other == Approach.East
    Approach.East -> other == Approach.North
    Approach.North -> other == Approach.West
    Approach.West -> other == Approach.South
}
