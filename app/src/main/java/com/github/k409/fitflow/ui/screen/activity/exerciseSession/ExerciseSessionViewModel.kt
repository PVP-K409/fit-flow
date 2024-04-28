package com.github.k409.fitflow.ui.screen.activity.exerciseSession

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class ExerciseSessionViewModel @Inject constructor(
    private val client : FusedLocationProviderClient,
) : ViewModel() {
    private val _distance = MutableStateFlow("0 km")
    private val _time = MutableStateFlow("0:00:00")
    private val _showMap = MutableStateFlow(false)
    private val _locations = MutableStateFlow<MutableList<LatLng>>(mutableListOf())
    private val _liveLocation = MutableStateFlow(LatLng(0.0, 0.0))


    val distance = _distance.asStateFlow()
    val time = _time.asStateFlow()
    val showMap = _showMap.asStateFlow()
    val locations = _locations.asStateFlow()
    val liveLocation = _liveLocation.asStateFlow()

    fun startSession() {
        _showMap.value = true
    }

    fun stopSession() {
        _showMap.value = false
    }

    fun updateDistance(newDistance: String) {
        _distance.value = newDistance
    }

    fun updateTime(newTime: String) {
        _time.value = newTime
    }

    fun getUserLocation() {
        client.lastLocation.addOnSuccessListener { location ->
            val latLng =
                LatLng(location.latitude, location.longitude)
            _locations.value.add(latLng)
            _liveLocation.value = latLng
        }

        Log.d("ExerciseSessionViewModel", "getUserLocation: ${_liveLocation.value}")
    }
}