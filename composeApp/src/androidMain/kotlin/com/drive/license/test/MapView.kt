package com.drive.license.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.drive.license.test.domain.model.LearningCenter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
actual fun MapView(
    centers: List<LearningCenter>,
    modifier: Modifier
) {
    val yerevan = LatLng(40.1872, 44.5152)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(yerevan, 11f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        centers.forEach { center ->
            Marker(
                state = MarkerState(position = LatLng(center.lat, center.lng)),
                title = center.name,
                snippet = center.address
            )
        }
    }
}
