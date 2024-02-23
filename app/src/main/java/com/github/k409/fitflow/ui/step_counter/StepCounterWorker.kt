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

private const val TAG = "StepCounterWorker"

@HiltWorker
class StepCounterWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: StepRepository,
    private val stepCounter: StepCounter
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting work...")
        // TODO: Remove this
        return Result.success()

        val today = LocalDate.now().toString()

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
            Log.d(TAG, "Updated steps: $newStep")

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating steps", e)
            return Result.retry()
        }

    }
}