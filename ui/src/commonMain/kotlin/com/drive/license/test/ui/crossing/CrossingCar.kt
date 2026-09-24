package com.drive.license.test.ui.crossing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.crossing_car_emergency
import drivelicensetest.ui.generated.resources.crossing_car_general
import drivelicensetest.ui.generated.resources.crossing_car_medical
import drivelicensetest.ui.generated.resources.crossing_car_police
import drivelicensetest.ui.generated.resources.crossing_car_roadwork
import org.jetbrains.compose.resources.painterResource

/**
 * Top-down sprites from Unlucky Studio's vehicle pack (CC0).
 * The nose points up, matching a heading of 0 (north) before the stage rotates the car.
 */
@Composable
fun CrossingCarArt(
    kind: VehicleKind,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(spriteFor(kind)),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
    )
}

private fun spriteFor(kind: VehicleKind) = when (kind) {
    VehicleKind.General -> Res.drawable.crossing_car_general
    VehicleKind.Medical -> Res.drawable.crossing_car_medical
    VehicleKind.Police -> Res.drawable.crossing_car_police
    VehicleKind.RoadWork -> Res.drawable.crossing_car_roadwork
    VehicleKind.Emergency -> Res.drawable.crossing_car_emergency
}
