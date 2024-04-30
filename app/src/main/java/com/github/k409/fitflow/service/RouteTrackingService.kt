package com.github.k409.fitflow.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.ExerciseSessionActivity
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.model.NotificationId
import com.github.k409.fitflow.model.getExerciseSessionActivityByType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_LOCATION_UPDATE_INTERVAL = 4000L
private const val DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL = 2000L

private val notificationChannel = NotificationChannel.ExerciseSession.channelId
private val notificationId = NotificationId.ExerciseSession.notificationId


typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class RouteTrackingService : LifecycleService() {

    @Inject lateinit var locationClient: FusedLocationProviderClient

    companion object {
        private var exerciseSessionActivity: ExerciseSessionActivity? = null
        private var locationUpdateInterval = DEFAULT_LOCATION_UPDATE_INTERVAL
        private var fastestLocationUpdateInterval = DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL
        val update = Channel<Unit>()
        val isTracking = MutableStateFlow(false)
        val sessionActive = MutableStateFlow(false)
        val sessionPaused = MutableStateFlow(false)
        val selectedExercise = MutableStateFlow("")
        val pathPoints = MutableStateFlow<Polylines>(mutableListOf())
        val fineLocationPermissions = listOf(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    enum class Actions {
        START, STOP, PAUSE, RESUME,
    }

    override fun onCreate() {
        super.onCreate()

        lifecycleScope.launch {
            isTracking.collect { isActive ->
                updateLocationTracking(isActive)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
            Actions.PAUSE.toString() -> pause()
            Actions.RESUME.toString() -> resume()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {
        isTracking.value = false
        sessionActive.value = false
        sessionPaused.value = false
        selectedExercise.value = ""
        pathPoints.value = mutableListOf()
        locationClient.removeLocationUpdates(locationCallback)
        stopSelf()
    }

    private fun start() {
        addEmptyPolyline()

        exerciseSessionActivity = getExerciseSessionActivityByType(selectedExercise.value)
        locationUpdateInterval = exerciseSessionActivity!!.locationUpdateInterval
        fastestLocationUpdateInterval = exerciseSessionActivity!!.fastestLocationUpdateInterval
        isTracking.value = true
        sessionActive.value = true

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(exerciseSessionNotificationChannel())

        startForeground(notificationId, exerciseSessionNotification())
    }
    private fun pause() {
        isTracking.value = false
        sessionPaused.value = true
    }



    private fun resume() {
        addEmptyPolyline()
        isTracking.value = true
        sessionPaused.value = false
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking) {
            if (hasLocationPermission()) {
                val request = LocationRequest.Builder(locationUpdateInterval).apply {
                    setMinUpdateIntervalMillis(fastestLocationUpdateInterval)
                    setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                }.build()

                locationClient.requestLocationUpdates(request,
                    locationCallback,
                    Looper.getMainLooper(),
                )
            }
        } else {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val position = LatLng(location.latitude, location.longitude)
            val updatedPathPoints = pathPoints.value.toMutableList()
            if (updatedPathPoints.isEmpty()) {
                updatedPathPoints.add(mutableListOf())
            }
            updatedPathPoints.last().add(position)
            pathPoints.value = updatedPathPoints
            lifecycleScope.launch {
                update.send(Unit)
            }
        }
    }

    private fun addEmptyPolyline() {
        val updatedPathPoints = pathPoints.value.toMutableList()
        updatedPathPoints.add(mutableListOf())
        pathPoints.value = updatedPathPoints
    }


    private fun exerciseSessionNotificationChannel(): android.app.NotificationChannel {
        val channelName = NotificationChannel.ExerciseSession
        val importance = NotificationManager.IMPORTANCE_HIGH
        return android.app.NotificationChannel(
            notificationChannel,
            channelName.toString(),
            importance,
        )
    }

    private fun exerciseSessionNotification(): Notification {
        val notificationTitle = getString(R.string.exercise_session_in_progress)
        val notificationText = selectedExercise.value

        val icon = exerciseSessionActivity?.icon ?: R.drawable.ic_launcher_foreground
        val iconBitmap = BitmapFactory.decodeResource(resources, icon)

        return NotificationCompat.Builder(this, notificationChannel)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(icon)
            .setLargeIcon(iconBitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .build()
    }

    private fun hasLocationPermission() : Boolean{
        return fineLocationPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}