package com.github.k409.fitflow.ui.step_counter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.DataModels.Step
import com.github.k409.fitflow.Database.StepRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate


private const val TAG = "StepCounterWorker"

@HiltWorker
class StepCounterWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: StepRepository,
    private val stepCounter: StepCounter,
    private val prefs: SharedPreferences
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val hasRebooted = prefs.getBoolean("rebooted", false) // boolean if reboot has happened
        val today = LocalDate.now().toString()

        try {
            val currentSteps = stepCounter.steps()
            val step: Step? = repository.loadTodaySteps(today)

            val newStep = if (step == null) { // if new day
                Step(
                    current = 0,
                    initial = currentSteps,
                    date = today,
                    temp = 0
                )
            } else if(hasRebooted || currentSteps <=1) { //if current day and reboot has happened
                Step(
                    current = step.current + currentSteps,
                    initial = 0,
                    date = today,
                    temp = step.current
                )
            }
            else{
                // if current day and no reboot
                Step(
                    current = currentSteps - step.initial + step.temp,
                    initial = step.initial,
                    date = today,
                    temp = step.temp
                )
            }
            repository.updateSteps(newStep)
            if (hasRebooted) { // we have handled the reboot
                prefs.edit().putBoolean("rebooted", false).apply()
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating steps", e)
            return Result.retry()
        }

    }
}