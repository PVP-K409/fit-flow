package com.github.k409.fitflow.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.R
import com.github.k409.fitflow.data.AquariumRepository
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.service.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class AquariumMetricsRemindersWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationService: NotificationService,
    private val aquariumRepository: AquariumRepository,
) : CoroutineWorker(
    context,
    workerParams,
) {

    companion object {
        const val WORKER_NAME = "com.github.k409.fitflow.worker.AquariumMetricsRemindersWorker"
    }

    override suspend fun doWork(): Result {
        try {
            val aquariumStats = aquariumRepository.getAquariumStats().first()

            val health = "${(aquariumStats.healthLevel * 100).toInt()}%"
            val water = "${(aquariumStats.waterLevel * 100).toInt()}%"

            val text = context.getString(R.string.aquarium_metrics_notification_text, health, water)

            notificationService.show(
                Notification(
                    channel = NotificationChannel.Default,
                    title = context.getString(R.string.aquarium_metrics),
                    text = text,
                )
            )

        } catch (e: Exception) {
            Log.e(WORKER_NAME, "Error: ", e)

            return Result.failure()
        }

        return Result.success()
    }
}
