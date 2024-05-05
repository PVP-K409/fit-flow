package com.github.k409.fitflow.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.github.k409.fitflow.R
import com.github.k409.fitflow.data.AquariumRepository
import com.github.k409.fitflow.data.GoalsRepository
import com.github.k409.fitflow.data.HEALTH_LEVEL_CHANGE_DAILY
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.data.WATER_LEVEL_CHANGE_DAILY
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.model.NotificationId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


private const val AquariumUpdate = "AquariumUpdate"
private val notificationChannel = NotificationChannel.AquariumHealth.channelId
private val notificationId = NotificationId.AquariumHealth.notificationId
@AndroidEntryPoint
class AquariumHealthService : Service() {

    @Inject lateinit var aquariumRepository: AquariumRepository
    @Inject lateinit var hydrationRepository: HydrationRepository
    @Inject lateinit var goalsRepository: GoalsRepository
    @Inject lateinit var  notificationService: NotificationService
    @Inject lateinit var notificationManager: NotificationManager
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            GoalUpdateService.Actions.START.toString() -> start()
            GoalUpdateService.Actions.STOP.toString() -> stopSelf()
        }
        return START_STICKY
    }

    enum class Actions {
        START, STOP
    }

    private fun start() {
        val aquariumHealthNotification = notificationService.createNotification(
            notificationTitle = getString(R.string.aquarium_health_update),
            notificationText = getString(R.string.updating_aquarium_health_metrics),
            notificationChannel = notificationChannel,
            setSmallIcon = R.drawable.ic_launcher_foreground,
            setPriority = NotificationCompat.PRIORITY_LOW,
            setAutoCancel = true,
        )
        val aquariumHealthNotificationChannel = notificationService.createNotificationChannel(
            channelName = NotificationChannel.AquariumHealth.toString(),
            channelId = notificationChannel,
            importance = NotificationManager.IMPORTANCE_LOW,
            setShowBadge = false,
            vibrationPattern = longArrayOf(0),
            enableLights = false,
            enableVibration = false,
        )
        notificationManager.createNotificationChannel(aquariumHealthNotificationChannel)

        startForeground(notificationId, aquariumHealthNotification)

        CoroutineScope(Dispatchers.Default).launch {
            try {
                performAquariumHealthUpdate()
                stopSelf()
            } catch (e: Exception) {
                Log.e(
                    AquariumUpdate,
                    "Failed to update aquarium metrics",
                    e,
                )
            }
        }

    }

    private suspend fun performAquariumHealthUpdate() {
        try {
            val now = LocalDateTime.now()
            val yesterday = now.minusDays(1).toLocalDate()

            // hydration
            val yesterdayHydrationRecord =
                hydrationRepository.getWaterIntake(yesterday.toString()).first()
            val hydrationGoal = hydrationRepository.getWaterIntakeGoal().first()

            if (yesterdayHydrationRecord.waterIntake < hydrationGoal) {
                aquariumRepository.changeWaterLevel(-WATER_LEVEL_CHANGE_DAILY)
            }

            // activity goals
            val dailyActivityGoals =
                goalsRepository.getDailyGoals(yesterday.toString()) ?: emptyMap()

            for (goal in dailyActivityGoals) {
                if (!goal.value.completed && goal.value.mandatory) {
                    aquariumRepository.changeHealthLevel(-HEALTH_LEVEL_CHANGE_DAILY)
                }
            }
        } catch (e: Exception) {
            Log.e(
                AquariumUpdate,
                "Failed to update aquarium metrics",
                e,
            )
        }
    }
}