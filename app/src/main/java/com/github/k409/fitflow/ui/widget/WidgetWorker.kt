package com.github.k409.fitflow.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.github.k409.fitflow.data.AquariumRepository
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.data.StepsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate

@HiltWorker
class WidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val aquariumRepository: AquariumRepository,
    private val hydrationRepository: HydrationRepository,
    private val stepsRepository: StepsRepository,
) : CoroutineWorker(
    context,
    workerParams,
) {
    companion object {

        private val uniqueWorkName = WidgetWorker::class.java.simpleName

        /**
         * Enqueues a new worker to refresh weather data only if not enqueued already
         *
         * Note: if you would like to have different workers per widget instance you could provide
         * the unique name based on some criteria (e.g selected weather location).
         *
         * @param force set to true to replace any ongoing work and expedite the request
         */
        fun enqueue(
            context: Context,
            force: Boolean = false
        ) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<WidgetWorker>(
                Duration.ofMinutes(30)
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            // Replace any enqueued work and expedite the request
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.UPDATE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
        }

        /**
         * Cancel any ongoing worker
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(FitFlowWidget::class.java)
        return try {
            // Update state to indicate loading
            setWidgetState(glanceIds, WidgetInfo.Loading)

            // Update state with new data
            val metrics = aquariumRepository.getAquariumStats().first()
            val stepsRecord = stepsRepository.getSteps(LocalDate.now().toString())
            val hydrationRecord = hydrationRepository.getTodayWaterIntake().first()

            val widgetState = WidgetInfo.Available(
                waterLevel = metrics.waterLevel,
                healthLevel = metrics.healthLevel,
                steps = stepsRecord?.totalSteps ?: 0,
                calories = stepsRecord?.caloriesBurned ?: 0,
                distance = stepsRecord?.totalDistance ?: 0.0,
                hydration = hydrationRecord.waterIntake
            )

            setWidgetState(glanceIds, widgetState)

            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, WidgetInfo.Unavailable(e.message.orEmpty()))
            if (runAttemptCount < 10) {
                // Exponential backoff strategy will avoid the request to repeat
                // too fast in case of failures.
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Update the state of all widgets and then force update UI
     */
    private suspend fun setWidgetState(
        glanceIds: List<GlanceId>,
        newState: WidgetInfo
    ) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = WidgetInfoDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        FitFlowWidget().updateAll(context)
    }
}