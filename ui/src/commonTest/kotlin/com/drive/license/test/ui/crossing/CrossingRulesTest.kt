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
    fun greenArrowLetsTheTurnGoBeforeOncomingTraffic() {
        val turning = CrossingVehicle(
            "south", "Ա", VehicleKind.General, Approach.South, Maneuver.TurnLeft,
        )
        val oncoming = CrossingVehicle(
            "north", "Բ", VehicleKind.General, Approach.North, Maneuver.Straight,
        )
        val arrow = JunctionControl.Lights(
            green = SignalPhase.NorthSouth,
            arrow = ArrowSignal(Approach.South, Maneuver.TurnLeft),
            arrowOnly = true,
        )
        assertEquals(false, mustYield(turning, oncoming, arrow))
        assertEquals(true, mustYield(oncoming, turning, arrow))
        val roundGreen = JunctionControl.Lights(SignalPhase.NorthSouth)
        assertEquals(true, mustYield(turning, oncoming, roundGreen))
        assertEquals(false, mustYield(oncoming, turning, roundGreen))
    }

    @Test
    fun reversingCarGivesWayEvenWhenItIsOnTheRight() {
        val forward = CrossingVehicle(
            "south", "Ա", VehicleKind.General, Approach.South, Maneuver.Straight,
        )
        val backing = CrossingVehicle(
            "east", "Բ", VehicleKind.General, Approach.East, Maneuver.Straight,
            facing = Facing.Reverse,
        )
        assertEquals(true, mustYield(backing, forward, JunctionControl.Equal))
        assertEquals(false, mustYield(forward, backing, JunctionControl.Equal))
        assertEquals(YieldCause.Reversing, yieldCause(backing, forward, JunctionControl.Equal))
        val forwardPose = poseAt(forward, 0.2f)
        val backingPose = poseAt(backing, 0.2f)
        val samePathForward = poseAt(backing.copy(facing = Facing.Forward), 0.2f)
        assertEquals(forwardPose.headingDegrees, poseAt(forward.copy(facing = Facing.Forward), 0.2f).headingDegrees)
        val turned = backingPose.headingDegrees - samePathForward.headingDegrees
        val wrapped = ((turned % 360f) + 360f) % 360f
        assertEquals(180f, wrapped, 1f)
    }

    @Test
    fun deckIncludesAReversingCar() {
        assertTrue(CrossingScenarios.any { scenario ->
            scenario.vehicles.any { it.facing == Facing.Reverse }
        })
    }

    @Test
    fun deckIncludesLightsAndEveryMainRoadBend() {
        assertTrue(CrossingScenarios.any { it.control is JunctionControl.Equal })
        assertTrue(CrossingScenarios.any { it.control is JunctionControl.Lights && it.control.ignoredSign == null })
        assertTrue(CrossingScenarios.any { it.control is JunctionControl.Lights && it.control.ignoredSign != null })
        assertTrue(CrossingScenarios.any { it.control is JunctionControl.Lights && it.control.arrow != null })
        for (bend in Bend.entries) {
            assertTrue(CrossingScenarios.any { it.control is JunctionControl.Sign && it.control.bend == bend })
        }
    }
}
