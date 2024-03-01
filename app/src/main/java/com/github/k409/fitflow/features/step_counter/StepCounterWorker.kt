package com.github.k409.fitflow.features.step_counter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.model.User
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate


private const val TAG = "StepCounterWorker"

@HiltWorker
class StepCounterWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: UserRepository,
    private val stepCounter: StepCounter,
    private val prefs: SharedPreferences
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val hasRebooted = prefs.getBoolean("rebooted", false)
        val today = LocalDate.now().toString()
        val user: User? = repository.getUser()

        try {
            val currentSteps = stepCounter.steps()
            val dailyStepRecord: DailyStepRecord? = repository.loadTodaySteps(today)
            val newDailyStepRecord: DailyStepRecord

            if (dailyStepRecord == null) { // if new day
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = 0,
                    initialSteps = currentSteps,
                    recordDate = today,
                    stepsBeforeReboot = 0,
                    caloriesBurned = 0,
                    totalDistance = 0.0
                )
            } else if (hasRebooted || currentSteps <= 1) { //if current day and reboot has happened
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = dailyStepRecord.totalSteps + currentSteps,
                    initialSteps = 0,
                    recordDate = today,
                    stepsBeforeReboot = dailyStepRecord.totalSteps,
                    caloriesBurned = calculateCaloriesFromSteps(
                        (dailyStepRecord.totalSteps + currentSteps), user
                    ),
                    totalDistance = calculateDistanceFromSteps(
                        (dailyStepRecord.totalSteps + currentSteps), user
                    )
                )

                prefs.edit().putBoolean("rebooted", false).apply() // we have handled reboot
            } else {
                // if current day and no reboot
                newDailyStepRecord = DailyStepRecord(
                    totalSteps = currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot,
                    initialSteps = dailyStepRecord.initialSteps,
                    recordDate = today,
                    stepsBeforeReboot = dailyStepRecord.stepsBeforeReboot,
                    caloriesBurned = calculateCaloriesFromSteps(
                        (currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot),
                        user
                    ),
                    totalDistance = calculateDistanceFromSteps(
                        (currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot),
                        user
                    )
                )
            }

            repository.updateSteps(newDailyStepRecord)

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating steps", e)

            return Result.retry()
        }

    }
}