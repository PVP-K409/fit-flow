package com.github.k409.fitflow.ui.step_counter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.DataModels.Step
import com.github.k409.fitflow.DataModels.User
import com.github.k409.fitflow.Database.UserRepository
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
        val hasRebooted = prefs.getBoolean("rebooted", false) // boolean if reboot has happened
        val distanceAndCaloriesUtil = DistanceAndCaloriesUtil()
        val today = LocalDate.now().toString()
        val user: User? = repository.getUser()

        try {
            val currentSteps = stepCounter.steps()
            val step: Step? = repository.loadTodaySteps(today)
            val newStep: Step
            if (step == null) { // if new day
                newStep = Step(
                    current = 0,
                    initial = currentSteps,
                    date = today,
                    temp = 0,
                    calories = 0,
                    distance = 0.0
                )
            }
            else if(hasRebooted || currentSteps <=1) { //if current day and reboot has happened
                newStep = Step(
                    current = step.current + currentSteps,
                    initial = 0,
                    date = today,
                    temp = step.current,
                    calories = distanceAndCaloriesUtil.calculateCaloriesFromSteps((step.current+currentSteps), user),
                    distance = distanceAndCaloriesUtil.calculateDistanceFromSteps((step.current+currentSteps), user)
                )
                prefs.edit().putBoolean("rebooted", false).apply() // we have handled reboot
            }
            else{
                // if current day and no reboot
                newStep = Step(
                    current = currentSteps - step.initial + step.temp,
                    initial = step.initial,
                    date = today,
                    temp = step.temp,
                    calories = distanceAndCaloriesUtil.calculateCaloriesFromSteps((currentSteps - step.initial + step.temp), user),
                    distance = distanceAndCaloriesUtil.calculateDistanceFromSteps((currentSteps - step.initial + step.temp), user)
                )
            }
            repository.updateSteps(newStep)
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating steps", e)
            return Result.retry()
        }

    }
}