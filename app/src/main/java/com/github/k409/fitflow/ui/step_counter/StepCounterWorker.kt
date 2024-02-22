package com.github.k409.fitflow.ui.step_counter

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.DataModels.Step
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate


@HiltWorker
class StepCounterWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val repository: StepRepository,
    val stepCounter: StepCounter
) : CoroutineWorker(appContext, workerParams){

    override suspend fun doWork(): Result {
        val today = LocalDate.now().toString()
        Log.d("STEP_COUNTER_WORKER", "INITIALIZED")
        try {
            val currentSteps = stepCounter.steps()
            val step: Step? = repository.loadTodaySteps(today)

            val newStep = if (step == null) {
                // new day
                Step(
                    current = 0,
                    initial = currentSteps,
                    date = today
                )
            } else {
                // we update current day step log
                Step(
                    current = currentSteps - step.initial,
                    initial = step.initial,
                    date = today
                )
            }

            repository.updateSteps(newStep)
            return Result.success()
        } catch (e: Exception) {
            Log.e("StepCounterWorker", "Error updating steps", e)
            return Result.retry()
        }

    }
}