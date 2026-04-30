package com.drive.license.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.drive.license.test.domain.model.LearningCenter
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation

@Composable
actual fun MapView(
    centers: List<LearningCenter>,
    modifier: Modifier
) {
    UIKitView(
        factory = {
            val mapView = MKMapView()
            val yerevan = CLLocationCoordinate2DMake(40.1872, 44.5152)
            mapView.setRegion(
                MKCoordinateRegionMakeWithDistance(yerevan, 30000.0, 30000.0),
                animated = false
            )
            centers.forEach { center ->
                val annotation = MKPointAnnotation()
                annotation.setCoordinate(CLLocationCoordinate2DMake(center.lat, center.lng))
                annotation.setTitle(center.name)
                annotation.setSubtitle(center.address)
                mapView.addAnnotation(annotation)
            }
            mapView
        },
        modifier = modifier
    )
}
