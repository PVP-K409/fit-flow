package com.github.k409.fitflow.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.service.AquariumHealthService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AquariumMetricsUpdaterWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(
    context,
    workerParams,
) {

    companion object {
        const val WORKER_NAME = "com.github.k409.fitflow.worker.AquariumMetricsUpdaterWorker"
    }

    override suspend fun doWork(): Result {
        try {
            Intent(context, AquariumHealthService::class.java).also {
                it.action = AquariumHealthService.Actions.START.toString()
                context.startForegroundService(it)
            }
        } catch (e: Exception) {
            Log.e(
                WORKER_NAME,
                "Failed to update aquarium metrics",
                e,
            )
            return Result.failure()
        }

        return Result.success()
    }
}
