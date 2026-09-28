package com.drive.license.test.ui.crossing

/**
 * Order at an uncontrolled junction, from the RA traffic rules:
 * - §28 blue beacon and siren: everyone else gives way
 * - A car going backwards gives way to a car going forwards
 * - §32 orange beacon: no priority
 * - §96 secondary road gives way to the main road, whatever the direction.
 *   The sign's thick line may go left, straight, or right; both of its arms are the main road.
 * - A working traffic light replaces that sign: red gives way to green.
 *   A green arrow allows only that turn. Oncoming traffic on red does not block it.
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
            remaining.none { other -> mustYield(vehicle, other, scenario.control) }
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
    control: JunctionControl,
): Boolean {
    if (vehicle.id == other.id) return false
    val vehiclePrivileged = vehicle.beacon == Beacon.BlueSiren
    val otherPrivileged = other.beacon == Beacon.BlueSiren
    if (vehiclePrivileged != otherPrivileged) return otherPrivileged

    val vehicleReversing = vehicle.facing == Facing.Reverse
    val otherReversing = other.facing == Facing.Reverse
    if (vehicleReversing != otherReversing) return vehicleReversing

    if (control is JunctionControl.Lights) {
        val vehicleGreen = movementIsGreen(vehicle, control)
        val otherGreen = movementIsGreen(other, control)
        if (vehicleGreen != otherGreen) return otherGreen
    }

    val vehicleOnMain = onPriorityRoad(vehicle, control)
    val otherOnMain = onPriorityRoad(other, control)
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
    control: JunctionControl,
): YieldCause? {
    if (!mustYield(vehicle, other, control)) return null
    val vehiclePrivileged = vehicle.beacon == Beacon.BlueSiren
    val otherPrivileged = other.beacon == Beacon.BlueSiren
    if (vehiclePrivileged != otherPrivileged) return YieldCause.BlueSiren
    val vehicleReversing = vehicle.facing == Facing.Reverse
    val otherReversing = other.facing == Facing.Reverse
    if (vehicleReversing != otherReversing) return YieldCause.Reversing
    if (control is JunctionControl.Lights) {
        val vehicleGreen = movementIsGreen(vehicle, control)
        val otherGreen = movementIsGreen(other, control)
        if (vehicleGreen != otherGreen) return YieldCause.TrafficLight
    }
    val vehicleOnMain = onPriorityRoad(vehicle, control)
    val otherOnMain = onPriorityRoad(other, control)
    if (vehicleOnMain != otherOnMain) return YieldCause.MainRoad
    if (isOncoming(vehicle.approach, other.approach) &&
        vehicle.maneuver == Maneuver.TurnLeft &&
        other.maneuver != Maneuver.TurnLeft
    ) {
        return YieldCause.LeftTurn
    }
    return YieldCause.FromTheRight
}

private fun onPriorityRoad(vehicle: CrossingVehicle, control: JunctionControl): Boolean {
    val sign = when (control) {
        JunctionControl.Equal -> return true
        is JunctionControl.Lights -> return true
        is JunctionControl.Sign -> control
    }
    return vehicle.approach in signArms(sign)
}

/** The two arms joined by the thick line on the main-road sign. */
fun signArms(sign: JunctionControl.Sign): Set<Approach> =
    setOf(sign.from, otherArm(sign.from, sign.bend))

fun movementIsGreen(vehicle: CrossingVehicle, lights: JunctionControl.Lights): Boolean {
    val arrow = lights.arrow
    val arrowMatch = arrow != null &&
        arrow.approach == vehicle.approach &&
        arrow.maneuver == vehicle.maneuver
    if (lights.arrowOnly) return arrowMatch
    return isGreen(vehicle.approach, lights.green) || arrowMatch
}

fun isGreen(approach: Approach, phase: SignalPhase): Boolean = when (phase) {
    SignalPhase.NorthSouth -> approach == Approach.North || approach == Approach.South
    SignalPhase.EastWest -> approach == Approach.East || approach == Approach.West
}

/** The arm the main road continues into, as seen by a driver entering from [from]. */
fun otherArm(from: Approach, bend: Bend): Approach = when (bend) {
    Bend.Left -> when (from) {
        Approach.South -> Approach.West
        Approach.West -> Approach.North
        Approach.North -> Approach.East
        Approach.East -> Approach.South
    }
    Bend.Straight -> when (from) {
        Approach.South -> Approach.North
        Approach.North -> Approach.South
        Approach.East -> Approach.West
        Approach.West -> Approach.East
    }
    Bend.Right -> when (from) {
        Approach.South -> Approach.East
        Approach.East -> Approach.North
        Approach.North -> Approach.West
        Approach.West -> Approach.South
    }
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
