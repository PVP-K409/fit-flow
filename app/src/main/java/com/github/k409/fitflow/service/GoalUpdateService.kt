package com.github.k409.fitflow.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import com.github.k409.fitflow.R
import com.github.k409.fitflow.data.AquariumRepository
import com.github.k409.fitflow.data.GoalsRepository
import com.github.k409.fitflow.data.HEALTH_LEVEL_CHANGE_DAILY
import com.github.k409.fitflow.data.HEALTH_LEVEL_CHANGE_WEEKLY
import com.github.k409.fitflow.data.HealthStatsManager
import com.github.k409.fitflow.data.StepsRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.model.NotificationId
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.model.getValidExerciseTypesByType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject


private const val goalUpdate = "Goal Update service"
private const val stepUpdate = "Step Update service"
private const val daily = "Daily"
private const val weekly = "Weekly"
private const val walking = "Walking"
private val notificationChannel = NotificationChannel.GoalUpdate.channelId
private val notificationId = NotificationId.GoalUpdate.notificationId

@AndroidEntryPoint
class GoalUpdateService : Service()  {

    @Inject lateinit var stepsRepository: StepsRepository
    @Inject lateinit var stepCounterService: StepCounterService
    @Inject lateinit var prefs: SharedPreferences
    @Inject lateinit var client: HealthConnectClient
    @Inject lateinit var healthStatsManager: HealthStatsManager
    @Inject lateinit var goalsRepository: GoalsRepository
    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var aquariumRepository: AquariumRepository
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        return START_NOT_STICKY
    }

    enum class Actions {
        START, STOP
    }

    private fun start() {
        val title = "Updating Data"
        val text = "Updating goals and steps data"

        val notificationAndroid =
            NotificationCompat.Builder(this, notificationChannel)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .build()

        startForeground(notificationId, notificationAndroid)

        CoroutineScope(Dispatchers.Default).launch {
            try {
                performStepsUpdate()
                performGoalUpdate()
                delay(5000)
            } catch (e: Exception) {
                Log.e("GoalUpdateService", "Error updating data", e)
            } finally {
                withContext(Dispatchers.Main) {
                    stopSelf()
                }
            }
        }
    }


    private suspend fun performStepsUpdate() {
        val hasRebooted = prefs.getBoolean("rebooted", false)
        val lastDate = prefs.getString("lastDate", "") // last update day
        val today = LocalDate.now().toString()
        val permissions = setOf(
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
        )

        val grantedPerms = client.permissionController.getGrantedPermissions()
        val permissionsGranted = grantedPerms.containsAll(permissions)

        try {
            val currentSteps = stepCounterService.steps()
            val dailyStepRecord: DailyStepRecord? = stepsRepository.getSteps(today)
            val newDailyStepRecord: DailyStepRecord
            var calories = dailyStepRecord?.caloriesBurned ?: 0L
            var distance = dailyStepRecord?.totalDistance ?: 0.0
            val healthConnectSteps = healthStatsManager.getTotalSteps(today, today).toLong()

            if (permissionsGranted) {
                calories = healthStatsManager.getTotalCalories()
                distance = healthStatsManager.getTotalDistance()
            }

            val stepGoal = if (dailyStepRecord == null || dailyStepRecord.stepGoal == 0L) {
                val goalService = GoalService(stepsRepository)
                goalService.calculateStepTarget(today, today, stepsRepository).toLong()
            } else {
                dailyStepRecord.stepGoal
            }

            if (dailyStepRecord == null) { // if new day
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = if (permissionsGranted) healthConnectSteps else 0L,
                    stepCounterSteps = 0,
                    initialSteps = currentSteps,
                    recordDate = today,
                    stepsBeforeReboot = 0,
                    caloriesBurned = calories,
                    totalDistance = distance,
                    stepGoal = stepGoal,
                )
            } else if (hasRebooted || currentSteps <= 1) { // if current day and reboot has happened
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = if (permissionsGranted) healthConnectSteps else dailyStepRecord.stepCounterSteps + currentSteps,
                    stepCounterSteps = dailyStepRecord.stepCounterSteps + currentSteps,
                    initialSteps = currentSteps,
                    recordDate = today,
                    stepsBeforeReboot = dailyStepRecord.stepCounterSteps + currentSteps,
                    caloriesBurned = calories,
                    totalDistance = distance,
                    stepGoal = stepGoal,
                )

                prefs.edit().putBoolean("rebooted", false).apply() // we have handled reboot
            } else if (today != lastDate) {
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = if (permissionsGranted) healthConnectSteps else dailyStepRecord.stepCounterSteps,
                    stepCounterSteps = dailyStepRecord.stepCounterSteps,
                    initialSteps = currentSteps,
                    recordDate = today,
                    stepsBeforeReboot = dailyStepRecord.stepCounterSteps,
                    caloriesBurned = if (calories > dailyStepRecord.caloriesBurned!!) calories else dailyStepRecord.caloriesBurned,
                    totalDistance = distance,
                    stepGoal = stepGoal,
                )
            } else {
                // if current day and no reboot
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = if (permissionsGranted) healthConnectSteps else currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot,
                    stepCounterSteps = currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot,
                    initialSteps = dailyStepRecord.initialSteps,
                    recordDate = today,
                    stepsBeforeReboot = dailyStepRecord.stepsBeforeReboot,
                    caloriesBurned = calories,
                    totalDistance = distance,
                    stepGoal = stepGoal,
                )
            }

            prefs.edit().putString("lastDate", today).apply() // saving last update day

            stepsRepository.updateSteps(newDailyStepRecord)

        } catch (e: Exception) {
            Log.e(stepUpdate, "Error updating steps", e)
        }
    }

    private suspend fun performGoalUpdate() {
        try {
            val stepPermission = setOf(
                HealthPermission.getReadPermission(StepsRecord::class),
                HealthPermission.getReadPermission(DistanceRecord::class),
            )
            val exercisePermission = setOf(
                HealthPermission.getReadPermission(DistanceRecord::class),
                HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            )

            val grantedPerms = client.permissionController.getGrantedPermissions()
            val grantedStepsPermission = grantedPerms.containsAll(stepPermission)
            val grantedExercisePermission = grantedPerms.containsAll(exercisePermission)

            val periods = setOf(daily, weekly)
            val date = LocalDate.now().toString()

            for (period in periods) {
                val goalsToUpdate = when (period) {
                    daily -> goalsRepository.getDailyGoals(date)
                    weekly -> goalsRepository.getWeeklyGoals(date)
                    else -> return
                }

                if (!goalsToUpdate.isNullOrEmpty()) {
                    for (key in goalsToUpdate.keys) {
                        val goal = goalsToUpdate[key]

                        if (key == walking && grantedExercisePermission) {
                            goalsToUpdate[key]?.currentProgress = healthStatsManager.getTotalSteps(
                                startDateString = goal?.startDate ?: "",
                                endDateString = goal?.endDate ?: "",
                            )
                        } else if (grantedStepsPermission) {
                            val validExerciseTypes = getValidExerciseTypesByType(key)
                            goalsToUpdate[key]?.currentProgress =
                                healthStatsManager.getTotalExerciseDistance(
                                    validExerciseTypes = validExerciseTypes,
                                    startDateString = goal?.startDate ?: "",
                                    endDateString = goal?.endDate ?: "",
                                )
                        } else {
                            continue
                        }

                        val updatedGoal = goalsToUpdate[key]

                        if (updatedGoal?.completed == false && updatedGoal.currentProgress > updatedGoal.target) {
                            goalsToUpdate[key]?.completed = true
                            userRepository.addCoinsAndXp(updatedGoal.points, updatedGoal.xp)

                            val changeValue =
                                if (period == weekly) HEALTH_LEVEL_CHANGE_WEEKLY else HEALTH_LEVEL_CHANGE_DAILY
                            aquariumRepository.changeHealthLevel(changeValue)
                        }
                    }

                    goalsRepository.updateGoals(goalsToUpdate, LocalDate.now().toString(), period)
                }
            }

        } catch (e: Exception) {
            Log.e(goalUpdate, "Goals failed to update", e)
        }
    }

}