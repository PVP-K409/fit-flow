package com.github.k409.fitflow.ui.screen.activity.exerciseLog

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.HealthStatsManager
import com.github.k409.fitflow.model.ExerciseRecord
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseSessionMapViewModel @Inject constructor(
    private val healthStatManager: HealthStatsManager,
) : ViewModel() {
    private var _exerciseRecord = MutableStateFlow(ExerciseRecord())
    val exerciseRecord: StateFlow<ExerciseRecord> = _exerciseRecord

    val map = MutableStateFlow<GoogleMap?>(null)

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun setGoogleMap(googleMap: GoogleMap) {
        map.value = googleMap
    }

    private fun addRoute() {
        val polylineOptions = PolylineOptions()
            .color(Color.Blue.toArgb())
            .width(10f)
        val routePoints = _exerciseRecord.value.exerciseRoute?.route ?: emptyList()
        val startLatLng = LatLng(routePoints.first().latitude, routePoints.first().longitude)
        val endLatLng = LatLng(routePoints.last().latitude, routePoints.last().longitude)

        if (routePoints.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()

            for (location in routePoints) {
                val latLng = LatLng(location.latitude, location.longitude)
                polylineOptions.add(latLng)
                boundsBuilder.include(latLng)
            }
            map.value?.addPolyline(polylineOptions)
            val bounds = boundsBuilder.build()
            val padding = 150
            map.value?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }

        map.value?.addCircle(
            CircleOptions()
                .center(startLatLng)
                .radius(5.0)
                .strokeColor(Color.Green.toArgb())
                .fillColor(Color.Green.toArgb())
                .strokeWidth(20f),
        )
        map.value?.addCircle(
            CircleOptions()
                .center(endLatLng)
                .radius(5.0)
                .strokeColor(Color.Red.toArgb())
                .fillColor(Color.Red.toArgb())
                .strokeWidth(20f),
        )
    }

    fun setExerciseRecordId(recordId: String?) {
        val id = recordId?.toInt()
        if (id != null) {
            viewModelScope.launch {
                _exerciseRecord.value = healthStatManager.getExerciseRecordById(id)
                addRoute()
                _loading.value = false
            }
        }
    }
}
