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

/** Nose follows the path, or the car backs along the same path. */
enum class Facing {
    Forward,
    Reverse,
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

/** How the thick line on the main-road sign leaves the arm it is read from. */
enum class Bend {
    Left,
    Straight,
    Right,
}

/** Which pair of opposite arms has a round green light. */
enum class SignalPhase {
    NorthSouth,
    EastWest,
}

/**
 * Extra green arrow on one approach.
 * When that approach's round light is red, rules §91 apply: the turn may go,
 * but it yields to vehicles moving from other directions.
 */
data class ArrowSignal(
    val approach: Approach,
    val maneuver: Maneuver,
)

/**
 * What controls the junction.
 * A working traffic light replaces the main-road sign.
 * [JunctionControl.Lights.ignoredSign] is still drawn, so the deck can show both.
 */
sealed class JunctionControl {
    data object Equal : JunctionControl()

    data class Sign(val from: Approach, val bend: Bend) : JunctionControl()

    data class Lights(
        val green: SignalPhase,
        val ignoredSign: Sign? = null,
        val arrow: ArrowSignal? = null,
    ) : JunctionControl()
}

/** The rule that makes one vehicle wait for another. */
enum class YieldCause {
    BlueSiren,
    MainRoad,
    TrafficLight,
    LeftTurn,
    FromTheRight,
    PermissiveArrow,
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
    val facing: Facing = Facing.Forward,
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
    val control: JunctionControl = JunctionControl.Equal,
    val notes: List<YieldNote> = emptyList(),
)
