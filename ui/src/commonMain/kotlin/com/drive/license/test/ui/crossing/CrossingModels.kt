package com.drive.license.test.ui.crossing

import org.jetbrains.compose.resources.StringResource

/** What the vehicle is. Paint and the legend use this; priority does not. */
enum class VehicleKind {
    General,
    Medical,
    Police,
    RoadWork,
    Emergency,
}

/** Which arm of the junction the vehicle is waiting on. */
enum class Approach {
    North,
    East,
    South,
    West,
}

/** How it leaves, once it is that vehicle's turn. */
enum class Maneuver {
    Straight,
    TurnLeft,
    TurnRight,
}

/**
 * Blue beacon plus siren is the only signal that gives way-priority (rules §28).
 * An orange beacon warns, and does not (rules §32).
 */
enum class Beacon {
    None,
    BlueSiren,
    Orange,
}

/** Which road is the main road. Null means the arms are equal. */
enum class RoadAxis {
    NorthSouth,
    EastWest,
}

/** The rule that makes one vehicle wait for another. */
enum class YieldCause {
    BlueSiren,
    MainRoad,
    LeftTurn,
    FromTheRight,
}

/** One step of the stored explanation: [waitingId] gives way to [aheadId]. */
data class YieldNote(
    val waitingId: String,
    val aheadId: String,
    val cause: YieldCause,
)

data class CrossingVehicle(
    val id: String,
    val label: String,
    val kind: VehicleKind,
    val approach: Approach,
    val maneuver: Maneuver,
    val beacon: Beacon = Beacon.None,
)

/**
 * One junction kept in the scenario list. [crossingOrder] is the answer:
 * vehicle ids in the order the animation drives them across.
 */
data class CrossingScenario(
    val id: String,
    val title: StringResource,
    val explanation: StringResource,
    val vehicles: List<CrossingVehicle>,
    val crossingOrder: List<String>,
    val priorityAxis: RoadAxis? = null,
    val notes: List<YieldNote> = emptyList(),
)
