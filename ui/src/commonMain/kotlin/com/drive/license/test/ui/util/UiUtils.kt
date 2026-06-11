package com.drive.license.test.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.category_general_traffic_rules
import drivelicensetest.ui.generated.resources.category_intersections_and_crossings
import drivelicensetest.ui.generated.resources.category_lane_usage_and_positioning
import drivelicensetest.ui.generated.resources.category_maneuvers_and_turns
import drivelicensetest.ui.generated.resources.category_prohibited_actions
import drivelicensetest.ui.generated.resources.category_right_of_way_and_priority
import drivelicensetest.ui.generated.resources.category_road_conditions_and_visibility
import drivelicensetest.ui.generated.resources.category_special_vehicles_and_situations
import drivelicensetest.ui.generated.resources.category_traffic_signs_and_markings
import drivelicensetest.ui.generated.resources.category_unknown
import drivelicensetest.ui.generated.resources.category_vehicle_types_and_categories
import org.jetbrains.compose.resources.stringResource

@Composable
fun formatCategoryName(raw: String): String {
    val res = when (raw.uppercase()) {
        "TRAFFIC_SIGNS_AND_MARKINGS" -> Res.string.category_traffic_signs_and_markings
        "LANE_USAGE_AND_POSITIONING" -> Res.string.category_lane_usage_and_positioning
        "MANEUVERS_AND_TURNS" -> Res.string.category_maneuvers_and_turns
        "RIGHT_OF_WAY_AND_PRIORITY" -> Res.string.category_right_of_way_and_priority
        "PROHIBITED_ACTIONS" -> Res.string.category_prohibited_actions
        "SPECIAL_VEHICLES_AND_SITUATIONS" -> Res.string.category_special_vehicles_and_situations
        "INTERSECTIONS_AND_CROSSINGS" -> Res.string.category_intersections_and_crossings
        "ROAD_CONDITIONS_AND_VISIBILITY" -> Res.string.category_road_conditions_and_visibility
        "VEHICLE_TYPES_AND_CATEGORIES" -> Res.string.category_vehicle_types_and_categories
        "GENERAL_TRAFFIC_RULES" -> Res.string.category_general_traffic_rules
        else -> Res.string.category_unknown
    }
    return stringResource(res)
}

@Composable
fun accuracyColor(accuracy: Float): Color = when {
    accuracy >= 0.8f -> MaterialTheme.colorScheme.primary
    accuracy >= 0.5f -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.error
}

/** Armenian uppercase labels used on official exam answer options (Ա, Բ, Գ, Դ, …). */
private val armenianAnswerLabels = listOf(
    "Ա", "Բ", "Գ", "Դ", "Ե", "Զ", "Է", "Ը", "Թ", "Ժ",
    "Ի", "Լ", "Խ", "Ծ", "Կ", "Հ", "Ձ", "Ղ", "Ճ", "Մ",
    "Յ", "Ն", "Շ", "Ո", "Չ", "Պ", "Ջ", "Ռ", "Ս", "Վ",
    "Տ", "Ր", "Ց", "Ւ", "Փ", "Ք", "Օ", "Ֆ",
)

fun answerOptionLabel(index: Int): String =
    armenianAnswerLabels.getOrElse(index) { "" }
