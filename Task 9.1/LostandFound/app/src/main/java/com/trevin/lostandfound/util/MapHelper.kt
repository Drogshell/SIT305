package com.trevin.lostandfound.util

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

object MapHelper {

    fun setUpMapUI(map: GoogleMap, isControlsEnabled: Boolean = true) {
        map.uiSettings.apply {
            isMyLocationButtonEnabled = isControlsEnabled
            isCompassEnabled = isControlsEnabled
            isMapToolbarEnabled = isControlsEnabled
            isScrollGesturesEnabled = isControlsEnabled
            isZoomGesturesEnabled = isControlsEnabled
            isTiltGesturesEnabled = isControlsEnabled
            isRotateGesturesEnabled = isControlsEnabled
        }
    }

    fun addMarker(
        map: GoogleMap,
        lat: Double,
        lng: Double,
        title: String? = null,
        description: String? = null
    ): Marker? {
        val markerOptions = MarkerOptions().position(LatLng(lat, lng))
        title?.let { markerOptions.contentDescription(it) }
        description?.let { markerOptions.contentDescription(description) }
        return map.addMarker(markerOptions)
    }

    fun moveCamToLocation(
        map: GoogleMap,
        lat: Double,
        lng: Double,
        zoom: Float = 16f
    ) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
    }

    fun animateCamBounds(map: GoogleMap, boundsBuilder: LatLngBounds.Builder, padding: Int = 100) {
        val bounds = boundsBuilder.build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

}