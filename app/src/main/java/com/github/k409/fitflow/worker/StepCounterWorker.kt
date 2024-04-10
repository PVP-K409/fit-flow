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
import com.github.k409.fitflow.service.GoalService
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

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating steps", e)

            return Result.retry()
        }
    }
}
