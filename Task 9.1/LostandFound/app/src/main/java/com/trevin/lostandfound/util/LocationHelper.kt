package com.trevin.lostandfound.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import java.util.Locale

object LocationHelper {

    const val LOCATION_PERMISSION_REQUEST = 1001

    fun requestLocationPermission(activity: Activity): Boolean {
        val perm = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(activity, perm) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        ActivityCompat.requestPermissions(activity, arrayOf(perm), LOCATION_PERMISSION_REQUEST)
        return false
    }

    // Get last known location or request update
    fun getCurrentLocation(
        activity: Activity,
        fusedClient: FusedLocationProviderClient,
        onSuccess: (lat: Double, lng: Double) -> Unit,
        onFailure: (error: String) -> Unit,
    ) {
        val perm = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
            onFailure("Location permission not granted")
            return
        }
        fusedClient.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    onSuccess(loc.latitude, loc.longitude)
                } else {
                    requestSingleLocationUpdate(fusedClient, onSuccess, onFailure)
                }
            }
            .addOnFailureListener { onFailure(it.message ?: "Unknown error") }
    }

    // Request a one-time location update
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun requestSingleLocationUpdate(
        fusedClient: FusedLocationProviderClient,
        onSuccess: (lat: Double, lng: Double) -> Unit,
        onFailure: (error: String) -> Unit,
    ) {
        val locRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setMaxUpdates(1).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation
                if (loc != null) {
                    onSuccess(loc.latitude, loc.longitude)
                } else {
                    onFailure("Unable to get location")
                }
                fusedClient.removeLocationUpdates(this)
            }
        }
        fusedClient.requestLocationUpdates(locRequest, callback, Looper.getMainLooper())
    }

    // Reverse geocode coordinates to address
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun reverseGeocode(
        activity: Activity,
        lat: Double,
        lng: Double,
        onSuccess: (address: String) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        val geo = Geocoder(activity, Locale.getDefault())
        geo.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: List<Address?>) {
                val addr = addresses.firstOrNull()?.getAddressLine(0) ?: "$lat, $lng"
                onSuccess(addr)
            }
            override fun onError(errorMessage: String?) {
                onFailure(errorMessage ?: "Failed to get address")
            }
        })
    }

}