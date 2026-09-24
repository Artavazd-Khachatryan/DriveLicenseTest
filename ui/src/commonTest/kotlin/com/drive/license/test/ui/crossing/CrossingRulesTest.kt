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
        assertEquals(false, mustYield(car, works, priorityAxis = null))
        assertEquals(true, mustYield(works, car, priorityAxis = null))
    }

    @Test
    fun twoSpecialVehiclesUseTheRightHandRule() {
        val ambulance = CrossingVehicle(
            "medic", "Շ", VehicleKind.Medical, Approach.West, Maneuver.Straight, Beacon.BlueSiren,
        )
        val police = CrossingVehicle(
            "police", "Ո", VehicleKind.Police, Approach.South, Maneuver.Straight, Beacon.BlueSiren,
        )
        assertEquals(true, mustYield(ambulance, police, priorityAxis = null))
        assertEquals(false, mustYield(police, ambulance, priorityAxis = null))
    }

    @Test
    fun mainRoadBeatsTheVehicleOnTheRight() {
        val onMain = CrossingVehicle("main", "Բ", VehicleKind.General, Approach.West, Maneuver.Straight)
        val onSide = CrossingVehicle("side", "Ա", VehicleKind.General, Approach.South, Maneuver.Straight)
        assertEquals(true, mustYield(onSide, onMain, RoadAxis.EastWest))
        assertEquals(false, mustYield(onMain, onSide, RoadAxis.EastWest))
        assertEquals(true, mustYield(onMain, onSide, priorityAxis = null))
    }
}
