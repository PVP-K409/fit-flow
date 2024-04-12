package com.github.k409.fitflow.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.data.HydrationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DrinkReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val hydrationRepository: HydrationRepository,
) : CoroutineWorker(
    context,
    workerParams,
) {

    companion object {
        const val WORKER_NAME = "com.github.k409.fitflow.worker.DrinkReminderWorker"
    }

    override suspend fun doWork(): Result {
        try {
            hydrationRepository.scheduleHydrationNotifications()
        } catch (e: Exception) {
            Log.e("DrinkReminderWorker", "Failed to schedule notifications", e)

            return Result.failure()
        }

        return Result.success()
    }
}
