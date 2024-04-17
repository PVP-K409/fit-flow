package com.github.k409.fitflow.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.data.AquariumRepository
import com.github.k409.fitflow.data.GoalsRepository
import com.github.k409.fitflow.data.HEALTH_LEVEL_CHANGE_DAILY
import com.github.k409.fitflow.data.HEALTH_LEVEL_CHANGE_WEEKLY
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.data.WATER_LEVEL_CHANGE_DAILY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

@HiltWorker
class AquariumMetricsUpdaterWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val aquariumRepository: AquariumRepository,
    private val goalsRepository: GoalsRepository,
    private val hydrationRepository: HydrationRepository,
) : CoroutineWorker(
    context,
    workerParams,
) {

    companion object {
        const val WORKER_NAME = "com.github.k409.fitflow.worker.AquariumMetricsUpdaterWorker"
    }

    override suspend fun doWork(): Result {
        try {
            val now = LocalDateTime.now()
            val yesterday = now.minusDays(1).toLocalDate()

            // hydration
            val yesterdayHydrationRecord =
                hydrationRepository.getWaterIntake(yesterday.toString()).first()
            val hydrationGoal = hydrationRepository.getWaterIntakeGoal().first()

            if (yesterdayHydrationRecord.waterIntake < hydrationGoal) {
                aquariumRepository.changeWaterLevel(-WATER_LEVEL_CHANGE_DAILY)
            }

            // activity goals
            val dailyActivityGoals =
                goalsRepository.getDailyGoals(yesterday.toString()) ?: emptyMap()
            val weeklyActivityGoals =
                goalsRepository.getWeeklyGoals(yesterday.toString()) ?: emptyMap()

            // TODO: validate logic below
            for (goal in dailyActivityGoals) {
                if (!goal.value.completed) {
                    aquariumRepository.changeHealthLevel(-HEALTH_LEVEL_CHANGE_DAILY)
                }
            }

            // TODO: validate logic below
            for (goal in weeklyActivityGoals) {
                if (!goal.value.completed) {
                    aquariumRepository.changeHealthLevel(-HEALTH_LEVEL_CHANGE_WEEKLY)
                }
            }


        } catch (e: Exception) {
            Log.e("DrinkReminderWorker", "Failed to schedule notifications", e)

            return Result.failure()
        }

        return Result.success()
    }
}
