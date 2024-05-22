package com.github.k409.fitflow.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.health.connect.client.records.ExerciseRoute
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.github.k409.fitflow.R
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.ExerciseSession
import com.github.k409.fitflow.model.ExerciseSessionActivity
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.model.NotificationId
import com.github.k409.fitflow.model.getExerciseSessionActivityByType
import com.github.k409.fitflow.ui.MainActivity
import com.github.k409.fitflow.util.formatTimeFromSeconds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

private const val DEFAULT_LOCATION_UPDATE_INTERVAL = 4000L
private const val DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL = 2000L
private const val TIMER_UPDATE_INTERVAL = 1000L

private val notificationChannelId = NotificationChannel.ExerciseSession.channelId
private val notificationId = NotificationId.ExerciseSession.notificationId

@AndroidEntryPoint
class RouteTrackingService : LifecycleService() {

    @Inject lateinit var locationClient: FusedLocationProviderClient

    @Inject lateinit var userRepository: UserRepository

    @Inject lateinit var healthConnectClient: HealthConnectService

    private lateinit var notificationBuilder: NotificationCompat.Builder

    private var exerciseSessionActivity: ExerciseSessionActivity? = null
    private var locationUpdateInterval = DEFAULT_LOCATION_UPDATE_INTERVAL
    private var fastestLocationUpdateInterval = DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL
    private val timeRunInMillis = MutableStateFlow(0L)
    private var userWeight: Double = 0.0

    companion object {
        val map = MutableStateFlow<GoogleMap?>(null)
        var circle: Circle? = null

        val timeRunInSecond = MutableStateFlow(0L)
        val distanceInKm = MutableStateFlow(0f)
        val avgSpeed = MutableStateFlow(0f)
        val calories = MutableStateFlow(0L)

        val exerciseSessionToWrite = MutableStateFlow(ExerciseSession())

        val isTracking = MutableStateFlow(false)
        val selectedExercise = MutableStateFlow("")
        val pathPoints = MutableStateFlow<MutableList<ExerciseRoute.Location>>(mutableListOf())
        val fineLocationPermissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        fun setGoogleMap(googleMap: GoogleMap) {
            map.value = googleMap
            initializeMap()
            addAllPolylines()
        }

        private fun initializeMap() {
            map.value?.let { map ->
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

                val newCameraPosition = CameraPosition.Builder()
                    .zoom(15f)
                    .bearing(currentCameraPosition.bearing)
                    .tilt(currentCameraPosition.tilt)

                val targetLatLng = if (pathPoints.value.isNotEmpty()) {
                    val lastPoint = pathPoints.value.last()
                    LatLng(lastPoint.latitude, lastPoint.longitude)
                } else {
                    currentCameraPosition.target
                }

                val circleOptions = CircleOptions()
                    .center(targetLatLng)
                    .radius(5.0)
                    .strokeColor(0x500000FF)
                    .fillColor(Color.Blue.toArgb())
                    .strokeWidth(20f)
                circle = map.addCircle(circleOptions)

                newCameraPosition.target(targetLatLng)

                map.moveCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition.build()))
            }
        }

        private fun addAllPolylines() {
            val polylineOptions = PolylineOptions()
                .color(Color.Blue.toArgb())
                .width(10f)

            for (pathPoint in pathPoints.value) {
                polylineOptions.add(LatLng(pathPoint.latitude, pathPoint.longitude))
            }

            map.value?.addPolyline(polylineOptions)
        }
    }

    enum class Actions {
        START, STOP
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
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {
        val time = timeRunInSecond.value
        if (time > 60) {
            exerciseSessionToWrite.value.endTime = Instant.now()
            exerciseSessionToWrite.value.endZoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())
            exerciseSessionToWrite.value.distance = distanceInKm.value
            exerciseSessionToWrite.value.calories = calories.value
            exerciseSessionToWrite.value.route = pathPoints.value
            val exerciseSession = exerciseSessionToWrite.value.copy()
            CoroutineScope(Dispatchers.Main).launch {
                healthConnectClient.writeExerciseSession(
                    exerciseSession.exerciseType,
                    exerciseSession.startTime,
                    exerciseSession.startZoneOffset,
                    exerciseSession.endTime,
                    exerciseSession.endZoneOffset,
                    exerciseSession.distance,
                    exerciseSession.calories,
                    exerciseSession.route,
                )
            }
        }
        exerciseSessionToWrite.value = ExerciseSession()
        isTracking.value = false
        selectedExercise.value = ""
        timeRunInMillis.value = 0L
        timeRunInSecond.value = 0L
        distanceInKm.value = 0f
        avgSpeed.value = 0f
        calories.value = 0L
        map.value = null
        pathPoints.value = mutableListOf()
        locationClient.removeLocationUpdates(locationCallback)
        stopSelf()
    }

    private fun start() {
        startTimer()

        exerciseSessionActivity = getExerciseSessionActivityByType(selectedExercise.value)
        locationUpdateInterval = exerciseSessionActivity!!.locationUpdateInterval
        fastestLocationUpdateInterval = exerciseSessionActivity!!.fastestLocationUpdateInterval

        exerciseSessionToWrite.value.exerciseType = exerciseSessionActivity!!.validExerciseType
        exerciseSessionToWrite.value.startTime = Instant.now()
        exerciseSessionToWrite.value.startZoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())

        isTracking.value = true

        CoroutineScope(Dispatchers.Main).launch {
            userWeight = userRepository.getUserWeight()
        }

        notificationBuilder = createNotificationChannelBuilder()

        startForeground(notificationId, notificationBuilder.build())
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (hasLocationPermission()) {
                val request = LocationRequest.Builder(locationUpdateInterval).apply {
                    setMinUpdateIntervalMillis(fastestLocationUpdateInterval)
                    setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                }.build()

                locationClient.requestLocationUpdates(
                    request,
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
            if (isTracking.value) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.value.size > 1) {
            val preLastLocation = pathPoints.value[pathPoints.value.size - 2]
            val lastLocation = pathPoints.value.last()
            val polylineOptions = PolylineOptions()
                .color(Color.Blue.toArgb())
                .width(10f)
                .add(LatLng(preLastLocation.latitude, preLastLocation.longitude))
                .add(LatLng(lastLocation.latitude, lastLocation.longitude))
            map.value?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.value.isNotEmpty()) {
            val currentZoomLevel = map.value?.cameraPosition?.zoom ?: 15f
            val position = LatLng(pathPoints.value.last().latitude, pathPoints.value.last().longitude)
            map.value?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    position,
                    currentZoomLevel,
                ),
            )
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val healthConnectLocation = ExerciseRoute.Location(
                latitude = location.latitude,
                longitude = location.longitude,
                time = Instant.now(),
            )
            val position = LatLng(location.latitude, location.longitude)
            val updatedPathPoints = pathPoints.value.toMutableList()

            updatedPathPoints.add(healthConnectLocation)
            if (updatedPathPoints.size > 1) {
                val distance = calculateDistance(
                    updatedPathPoints[updatedPathPoints.size - 2].latitude,
                    updatedPathPoints[updatedPathPoints.size - 2].longitude,
                    updatedPathPoints.last().latitude,
                    updatedPathPoints.last().longitude,
                )

                distanceInKm.value += distance / 1000

                if (timeRunInSecond.value > 0 && distanceInKm.value > 0.01f) {
                    avgSpeed.value = distanceInKm.value / (timeRunInSecond.value / 3600.toFloat())
                } else {
                    avgSpeed.value = 0f
                }
                calories.value = calculateCalories(exerciseSessionActivity!!.met.toFloat(), userWeight, timeRunInSecond.value / 3600.toFloat())
            }
            pathPoints.value = updatedPathPoints
            addLatestPolyline()
            moveCameraToUser()
            circle?.center = position
        }
    }

    private fun updateNotification() {
        val notificationText = "${selectedExercise.value}: ${formatTimeFromSeconds(timeRunInSecond.value)} "
        val notification = notificationBuilder
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannelBuilder(): NotificationCompat.Builder {
        val notificationTitle = getString(R.string.exercise_session_in_progress)
        val notificationText = "${selectedExercise.value}: ${formatTimeFromSeconds(timeRunInSecond.value)} "

        val icon = exerciseSessionActivity?.icon ?: R.drawable.ic_launcher_foreground

        val intent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setContentIntent(pendingIntent)
        return notificationBuilder
    }

    private fun hasLocationPermission(): Boolean {
        return fineLocationPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private var timeStarted = 0L
    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var lastSecondTimestamp = 0L
    private fun startTimer() {
        isTimerEnabled = true
        timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value) {
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.value = timeRun + lapTime

                if (timeRunInMillis.value >= lastSecondTimestamp + 1000) {
                    timeRunInSecond.value++
                    lastSecondTimestamp += 1000L
                    updateNotification()
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double,
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results)
        return results[0]
    }

    private fun calculateCalories(met: Float, weightInKg: Double, durationInHours: Float): Long {
        return (met * weightInKg * durationInHours).toLong()
    }
}
