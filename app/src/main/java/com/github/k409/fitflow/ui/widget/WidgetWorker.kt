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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        fun enqueue(
            context: Context,
            force: Boolean = false
        ) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<WidgetWorker>(
                Duration.ofMinutes(30)
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
        }

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
                hydration = hydrationRecord.waterIntake,
                lastUpdated = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )

            setWidgetState(glanceIds, widgetState)

            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, WidgetInfo.Unavailable(e.message.orEmpty()))

            if (runAttemptCount < 10) {
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