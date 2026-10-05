package com.drive.license.test.ui.crossing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CrossingRulesTest {

    @Test
    fun everyExampleHasTheLegalOrder() {
        assertTrue(CrossingScenarios.size >= 100, "deck has ${CrossingScenarios.size}")
        assertEquals(
            CrossingScenarios.map { it.id }.toSet().size,
            CrossingScenarios.size,
        )
        CrossingScenarios.forEach { scenario ->
            val legal = resolveCrossing(scenario).map { it.id }
            assertEquals(legal, scenario.crossingOrder)
            assertEquals(
                scenario.vehicles.map { it.approach }.distinct().size,
                scenario.vehicles.size,
            )
        }
    }

    @Test
    fun orangeBeaconDoesNotBeatTheCarOnTheRight() {
        val works = CrossingVehicle(
            "works", "Ճ", VehicleKind.RoadWork, Approach.West, Maneuver.Straight, Beacon.Orange,
        )
        val car = CrossingVehicle(
            "car", "Ա", VehicleKind.General, Approach.South, Maneuver.Straight,
        )
        assertEquals(false, mustYield(car, works, JunctionControl.Equal))
        assertEquals(true, mustYield(works, car, JunctionControl.Equal))
    }

    @Test
    fun twoSpecialVehiclesUseTheRightHandRule() {
        val ambulance = CrossingVehicle(
            "medic", "Շ", VehicleKind.Medical, Approach.West, Maneuver.Straight, Beacon.BlueSiren,
        )
        val police = CrossingVehicle(
            "police", "Ո", VehicleKind.Police, Approach.South, Maneuver.Straight, Beacon.BlueSiren,
        )
        assertEquals(true, mustYield(ambulance, police, JunctionControl.Equal))
        assertEquals(false, mustYield(police, ambulance, JunctionControl.Equal))
    }

    @Test
    fun mainRoadBeatsTheVehicleOnTheRight() {
        val onMain = CrossingVehicle("main", "Բ", VehicleKind.General, Approach.West, Maneuver.Straight)
        val onSide = CrossingVehicle("side", "Ա", VehicleKind.General, Approach.South, Maneuver.Straight)
        val eastWest = JunctionControl.Sign(Approach.West, Bend.Straight)
        assertEquals(true, mustYield(onSide, onMain, eastWest))
        assertEquals(false, mustYield(onMain, onSide, eastWest))
        assertEquals(true, mustYield(onMain, onSide, JunctionControl.Equal))
    }

    @Test
    fun mainRoadSignBendsLeftStraightAndRight() {
        val side = CrossingVehicle("side", "Ա", VehicleKind.General, Approach.East, Maneuver.Straight)
        val fromSouth = CrossingVehicle("main", "Բ", VehicleKind.General, Approach.South, Maneuver.Straight)
        for (bend in Bend.entries) {
            val sign = JunctionControl.Sign(Approach.South, bend)
            val onMain = fromSouth.approach in signArms(sign) || side.approach in signArms(sign)
            assertTrue(onMain)
            if (side.approach in signArms(sign)) {
                assertEquals(false, mustYield(side, fromSouth, sign))
            } else {
                assertEquals(true, mustYield(side, fromSouth, sign))
            }
        }
        assertEquals(
            setOf(Approach.South, Approach.West),
            signArms(JunctionControl.Sign(Approach.South, Bend.Left)),
        )
        assertEquals(
            setOf(Approach.South, Approach.North),
            signArms(JunctionControl.Sign(Approach.South, Bend.Straight)),
        )
        assertEquals(
            setOf(Approach.South, Approach.East),
            signArms(JunctionControl.Sign(Approach.South, Bend.Right)),
        )
    }

    @Test
    fun trafficLightReplacesTheMainRoadSign() {
        val green = CrossingVehicle("west", "Ա", VehicleKind.General, Approach.West, Maneuver.Straight)
        val red = CrossingVehicle("south", "Բ", VehicleKind.General, Approach.South, Maneuver.Straight)
        val lights = JunctionControl.Lights(
            SignalPhase.EastWest,
            ignoredSign = JunctionControl.Sign(Approach.South, Bend.Straight),
        )
        assertEquals(true, mustYield(red, green, lights))
        assertEquals(false, mustYield(green, red, lights))
        assertEquals(YieldCause.TrafficLight, yieldCause(red, green, lights))
        val signOnly = JunctionControl.Sign(Approach.South, Bend.Straight)
        assertEquals(true, mustYield(green, red, signOnly))
    }

    @Test
    fun permissiveArrowYieldsToTrafficFromOtherDirections() {
        val turning = CrossingVehicle(
            "south", "Ա", VehicleKind.General, Approach.South, Maneuver.TurnLeft,
        )
        val crossing = CrossingVehicle(
            "west", "Բ", VehicleKind.General, Approach.West, Maneuver.Straight,
        )
        val arrow = JunctionControl.Lights(
            green = SignalPhase.EastWest,
            arrow = ArrowSignal(Approach.South, Maneuver.TurnLeft),
        )
        assertEquals(true, mustYield(turning, crossing, arrow))
        assertEquals(false, mustYield(crossing, turning, arrow))
        assertEquals(YieldCause.PermissiveArrow, yieldCause(turning, crossing, arrow))
        assertEquals(true, movementIsGreen(turning, arrow))
        assertEquals(false, movementIsGreen(
            turning.copy(maneuver = Maneuver.Straight),
            arrow,
        ))
        val oncoming = CrossingVehicle(
            "north", "Գ", VehicleKind.General, Approach.North, Maneuver.Straight,
        )
        val roundGreen = JunctionControl.Lights(
            green = SignalPhase.NorthSouth,
            arrow = ArrowSignal(Approach.South, Maneuver.TurnLeft),
        )
        assertEquals(true, mustYield(turning, oncoming, roundGreen))
        assertEquals(false, mustYield(oncoming, turning, roundGreen))
        assertEquals(YieldCause.LeftTurn, yieldCause(turning, oncoming, roundGreen))
    }

    @Test
    fun reversingDoesNotChangeWhoGoesFirst() {
        val forward = CrossingVehicle(
            "south", "Ա", VehicleKind.General, Approach.South, Maneuver.Straight,
        )
        val backing = CrossingVehicle(
            "east", "Բ", VehicleKind.General, Approach.East, Maneuver.Straight,
            facing = Facing.Reverse,
        )
        assertEquals(
            mustYield(forward, backing.copy(facing = Facing.Forward), JunctionControl.Equal),
            mustYield(forward, backing, JunctionControl.Equal),
        )
        assertEquals(YieldCause.FromTheRight, yieldCause(forward, backing, JunctionControl.Equal))
        val backingPose = poseAt(backing, 0.2f)
        val samePathForward = poseAt(backing.copy(facing = Facing.Forward), 0.2f)
        val turned = backingPose.headingDegrees - samePathForward.headingDegrees
        val wrapped = ((turned % 360f) + 360f) % 360f
        assertEquals(180f, wrapped, 1f)
    }

    @Test
    fun everyCarFacesTheWayItDrives() {
        CrossingScenarios.forEach { scenario ->
            scenario.vehicles.forEach { vehicle ->
                val start = poseAt(vehicle, 0f)
                val end = poseAt(vehicle, 1f)
                val flip = if (vehicle.facing == Facing.Reverse) 180f else 0f
                checkClose(
                    norm(noseAtStart(vehicle.approach) + flip),
                    norm(start.headingDegrees),
                    "${scenario.id} ${vehicle.label} ${vehicle.approach} ${vehicle.maneuver} start",
                )
                checkClose(
                    norm(noseAtEnd(vehicle.approach, vehicle.maneuver) + flip),
                    norm(end.headingDegrees),
                    "${scenario.id} ${vehicle.label} ${vehicle.approach} ${vehicle.maneuver} end",
                )
            }
        }
    }

    @Test
    fun wrongOrderBlamesARealYieldNotTheHorizontalNeighbour() {
        // East and West both drive straight in parallel lanes: their paths never meet.
        val east = CrossingVehicle("east", "Ա", VehicleKind.General, Approach.East, Maneuver.Straight)
        val south = CrossingVehicle("south", "Բ", VehicleKind.General, Approach.South, Maneuver.Straight)
        val west = CrossingVehicle("west", "Գ", VehicleKind.General, Approach.West, Maneuver.Straight)
        val control = JunctionControl.Equal
        // Legal order: east, south, west (each yields to the car on its right).
        // The user answers west, east, south: west's mistake is cutting off south, not east.
        val violation = firstYieldViolation(listOf(west, east, south), control)
        assertEquals("west", violation?.waitingId)
        assertEquals("south", violation?.aheadId)
        assertEquals(YieldCause.FromTheRight, violation?.cause)
        // A legal order reports no violation.
        assertEquals(null, firstYieldViolation(listOf(east, south, west), control))
        // Two straight cars in opposite lanes owe each other nothing.
        assertEquals(false, mustYield(east, west, control))
        assertEquals(false, mustYield(west, east, control))
    }

    @Test
    fun everyStoredOrderIsTheOnlyLegalOne() {
        CrossingScenarios.forEach { scenario ->
            val order = scenario.crossingOrder.map { id ->
                scenario.vehicles.first { it.id == id }
            }
            assertEquals(null, firstYieldViolation(order, scenario.control), scenario.id)
            scenario.notes.forEach { note ->
                val waiting = scenario.vehicles.first { it.id == note.waitingId }
                val ahead = scenario.vehicles.first { it.id == note.aheadId }
                assertEquals(note.cause, yieldCause(waiting, ahead, scenario.control), scenario.id)
            }
        }
    }

    @Test
    fun deckDoesNotReverseThroughTheJunction() {
        assertTrue(CrossingScenarios.none { scenario ->
            scenario.vehicles.any { it.facing == Facing.Reverse }
        })
    }

    @Test
    fun deckIncludesEqualRoadsAndEveryMainRoadBend() {
        assertTrue(CrossingScenarios.any { it.control is JunctionControl.Equal })
        for (bend in Bend.entries) {
            assertTrue(CrossingScenarios.any { it.control is JunctionControl.Sign && it.control.bend == bend })
        }
    }

    @Test
    fun deckOmitsLightsAndUnsignedTwoCarJunctions() {
        assertTrue(CrossingScenarios.none { it.control is JunctionControl.Lights })
        assertTrue(
            CrossingScenarios.none { scenario ->
                scenario.vehicles.size == 2 && scenario.control !is JunctionControl.Sign
            },
        )
        assertTrue(
            CrossingScenarios.none { scenario ->
                scenario.vehicles.size == 2 &&
                    scenario.vehicles.all { it.maneuver == Maneuver.Straight }
            },
        )
    }
}

private fun checkClose(expected: Float, actual: Float, where: String) {
    val delta = kotlin.math.abs(norm(actual - expected))
    check(delta <= 8f) { "$where expected $expected actual $actual" }
}

private fun norm(degrees: Float): Float {
    var wrapped = degrees % 360f
    if (wrapped > 180f) wrapped -= 360f
    if (wrapped < -180f) wrapped += 360f
    return wrapped
}

private fun noseAtStart(approach: Approach): Float = when (approach) {
    Approach.South -> 0f
    Approach.North -> 180f
    Approach.West -> 90f
    Approach.East -> -90f
}

private fun noseAtEnd(approach: Approach, maneuver: Maneuver): Float = when (maneuver) {
    Maneuver.Straight -> noseAtStart(approach)
    Maneuver.TurnRight -> when (approach) {
        Approach.South -> 90f
        Approach.West -> 180f
        Approach.North -> -90f
        Approach.East -> 0f
    }
    Maneuver.TurnLeft -> when (approach) {
        Approach.South -> -90f
        Approach.West -> 0f
        Approach.North -> 90f
        Approach.East -> 180f
    }
}
