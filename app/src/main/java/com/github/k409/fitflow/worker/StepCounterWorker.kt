package com.github.k409.fitflow.worker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.data.HealthStatsManager
import com.github.k409.fitflow.data.StepsRepository
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.service.StepCounterService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

private const val TAG = "StepCounterWorker"

@HiltWorker
class StepCounterWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val stepsRepository: StepsRepository,
    private val stepCounterService: StepCounterService,
    private val prefs: SharedPreferences,
    private val client: HealthConnectClient,
    private val healthStatsManager: HealthStatsManager,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val hasRebooted = prefs.getBoolean("rebooted", false)
        val lastDate = prefs.getString("lastDate", "") // last update day
        val today = LocalDate.now().toString()
        val permissions = setOf(
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
        )

        val grantedPerms = client.permissionController.getGrantedPermissions()
        val granted = grantedPerms.containsAll(permissions)

        try {
            val currentSteps = stepCounterService.steps()
            val dailyStepRecord: DailyStepRecord? = stepsRepository.getSteps(today)
            val newDailyStepRecord: DailyStepRecord
            var calories = 0L
            var distance = 0.0

            if (granted) {
                calories = healthStatsManager.getTotalCalories()
                distance = healthStatsManager.getTotalDistance()
            }

            if (dailyStepRecord == null) { // if new day
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = 0,
                    initialSteps = currentSteps,
                    recordDate = today,
                    stepsBeforeReboot = 0,
                    caloriesBurned = calories,
                    totalDistance = distance,
                )
            } else if (hasRebooted || currentSteps <= 1) { // if current day and reboot has happened
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = dailyStepRecord.totalSteps + currentSteps,
                    initialSteps = currentSteps,
                    recordDate = today,
                    stepsBeforeReboot = dailyStepRecord.totalSteps + currentSteps,
                    caloriesBurned = calories,
                    totalDistance = distance,
                )

                prefs.edit().putBoolean("rebooted", false).apply() // we have handled reboot
            } else if (today != lastDate) {
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = dailyStepRecord.totalSteps,
                    initialSteps = currentSteps,
                    recordDate = today,
                    stepsBeforeReboot = dailyStepRecord.totalSteps,
                    caloriesBurned = if (calories > dailyStepRecord.caloriesBurned!!) calories else dailyStepRecord.caloriesBurned,
                    totalDistance = distance,
                )
            } else {
                // if current day and no reboot
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot,
                    initialSteps = dailyStepRecord.initialSteps,
                    recordDate = today,
                    stepsBeforeReboot = dailyStepRecord.stepsBeforeReboot,
                    caloriesBurned = calories,
                    totalDistance = distance,
                )
            }

            prefs.edit().putString("lastDate", today).apply() // saving last update day

            stepsRepository.updateSteps(newDailyStepRecord)

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating steps", e)

            return Result.retry()
        }
    }
}
