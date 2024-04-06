package com.github.k409.fitflow.worker

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.data.GoalsRepository
import com.github.k409.fitflow.data.HealthStatsManager
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.getValidExerciseTypesByType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

private const val TAG = "GoalUpdaterWorker"
private const val daily = "Daily"
private const val weekly = "Weekly"
private const val walking = "Walking"

@HiltWorker
class GoalUpdaterWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val goalsRepository: GoalsRepository,
    private val userRepository: UserRepository,
    private val healthStatsManager: HealthStatsManager,
    private val client: HealthConnectClient,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val stepPermission = setOf(
                HealthPermission.getReadPermission(StepsRecord::class),
                HealthPermission.getReadPermission(DistanceRecord::class),
            )
            val exercisePermission = setOf(
                HealthPermission.getReadPermission(DistanceRecord::class),
                HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            )

            val grantedPerms = client.permissionController.getGrantedPermissions()
            val grantedStepsPermission = grantedPerms.containsAll(stepPermission)
            val grantedExercisePermission = grantedPerms.containsAll(exercisePermission)

            val periods = setOf(daily, weekly)
            val date = LocalDate.now().toString()

            for (period in periods) {
                val goalsToUpdate = when (period) {
                    daily -> goalsRepository.getDailyGoals(date)
                    weekly -> goalsRepository.getWeeklyGoals(date)
                    else -> return Result.failure()
                }

                if (!goalsToUpdate.isNullOrEmpty()) {
                    for (key in goalsToUpdate.keys) {
                        val goal = goalsToUpdate[key]

                        if (key == walking && grantedExercisePermission) {
                            goalsToUpdate[key]?.currentProgress = healthStatsManager.getTotalSteps(
                                startDateString = goal?.startDate ?: "",
                                endDateString = goal?.endDate ?: "",
                            )
                        } else if (grantedStepsPermission) {
                            val validExerciseTypes = getValidExerciseTypesByType(key)
                            goalsToUpdate[key]?.currentProgress = healthStatsManager.getTotalExerciseDistance(
                                validExerciseTypes = validExerciseTypes,
                                startDateString = goal?.startDate ?: "",
                                endDateString = goal?.endDate ?: "",
                            )
                        } else {
                            continue
                        }

                        val updatedGoal = goalsToUpdate[key]

                        if (updatedGoal?.completed == false && updatedGoal.currentProgress > updatedGoal.target) {
                            goalsToUpdate[key]?.completed = true
                            userRepository.addCoinsAndXp(updatedGoal.points, updatedGoal.xp)
                        }
                    }

                    goalsRepository.updateGoals(goalsToUpdate, LocalDate.now().toString(), period)
                }
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "GoalUpdaterWorker failed to update")
            return Result.retry()
        }
    }
}
