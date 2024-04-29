package com.github.k409.fitflow.ui.screen.activity.exerciseSession

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.service.RouteTrackingService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseSessionViewModel @Inject constructor(
) : ViewModel() {
    private val _showMap = MutableStateFlow(false)
    private val _map = MutableStateFlow<GoogleMap?>(null)
    val isTracking = RouteTrackingService.isTracking.asStateFlow()
    private val pathPoints = RouteTrackingService.pathPoints.asStateFlow()
    var map = _map.asStateFlow()


    init {
        viewModelScope.launch {
            subscribeToObservers()
        }
    }
    fun setupMap(googleMap: GoogleMap) {
        _map.value = googleMap
        initializeMap()
        addAllPolylines()
    }



    private suspend fun subscribeToObservers() {
        while (true) {
                delay(1000)
                addLatestPolyline()
                moveCameraToUser()
        }
    }

    private fun initializeMap() {
        _map.value?.let { map ->
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isZoomGesturesEnabled = true
            map.uiSettings.isScrollGesturesEnabled = true
            map.uiSettings.isRotateGesturesEnabled = true
            map.uiSettings.isTiltGesturesEnabled = true
            map.uiSettings.isCompassEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            map.uiSettings.isIndoorLevelPickerEnabled = true
            map.uiSettings.isMapToolbarEnabled = true
            map.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true
            val currentCameraPosition = map.cameraPosition
            val initialLatLng = LatLng(54.687157, 25.279652)
            val newCameraPosition = CameraPosition.Builder()
                .target(initialLatLng)
                .zoom(15f)
                .bearing(currentCameraPosition.bearing)
                .tilt(currentCameraPosition.tilt)
                .build()
            map.moveCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition))
        }
    }

    fun startSession() {
        _showMap.value = true
    }

    fun stopSession() {
        _showMap.value = false
    }

    private fun addLatestPolyline() {
        if (pathPoints.value.isNotEmpty() && pathPoints.value.last().size > 1 ) {
            val preLastLatLng = pathPoints.value.last()[pathPoints.value.last().size - 2]
            val lastLatLng = pathPoints.value.last().last()
            val polylineOptions = PolylineOptions()
                .color(Color.Blue.toArgb())
                .width(10f)
                .add(preLastLatLng)
                .add(lastLatLng)
            map.value?.addPolyline(polylineOptions)
        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints.value) {
            val polylineOptions = PolylineOptions()
                .color(Color.Blue.toArgb())
                .width(10f)
                .addAll(polyline)
            map.value?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.value.isNotEmpty() && pathPoints.value.last().isNotEmpty()) {
            val currentZoomLevel = _map.value?.cameraPosition?.zoom ?: 15f
            map.value?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.value.last().last(),
                    currentZoomLevel
                )
            )
        }
    }
}