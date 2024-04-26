package com.github.k409.fitflow.worker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.R
import com.github.k409.fitflow.data.GoalsRepository
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.model.NotificationId
import com.github.k409.fitflow.service.GoalUpdateService
import com.github.k409.fitflow.service.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

private const val TAG = "GoalAndStepUpdateWorker"
private const val walking = "Walking"
private val notificationId = NotificationId.WalkingProgress.notificationId

@HiltWorker
class GoalAndStepUpdateWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val goalsRepository: GoalsRepository,
    private val notificationService: NotificationService,
    private val client: HealthConnectClient,
    private val sharedPreferences: SharedPreferences,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORKER_NAME = "com.github.k409.fitflow.worker.GoalAndStepUpdateWorker"
        const val startTimeToSend = 11
        const val endTimeToSend = 22
    }

    override suspend fun doWork(): Result {
        return try {
            Intent(appContext, GoalUpdateService::class.java).also {
                it.action = GoalUpdateService.Actions.START.toString()
                appContext.startForegroundService(it)
            }

            while (goalsUpdatedCheck()) {
                delay(3000)
            }

            val stepPermission = setOf(
                HealthPermission.getReadPermission(StepsRecord::class),
                HealthPermission.getReadPermission(DistanceRecord::class),
                HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            )

            val grantedPerms = client.permissionController.getGrantedPermissions()
            val grantedStepsPermission = grantedPerms.containsAll(stepPermission)

            // Post notification after update
            if (isHourWithinRange() && grantedStepsPermission) {
                val date = LocalDate.now().toString()
                val walkingGoal = goalsRepository.getDailyGoals(date)?.get(walking)

                if (walkingGoal != null) {
                    val progress = walkingGoal.currentProgress.toInt()
                    val target = walkingGoal.target.toInt()
                    if (progress >= target) {
                        return Result.success()
                    }
                    val notification = Notification(
                        id = notificationId,
                        channel = NotificationChannel.WalkingProgress,
                        title = appContext.getString(R.string.walking_goal_progress),
                        text = appContext.getString(
                            R.string.you_have_walked_out_of_steps_today,
                            progress.toString(),
                            target.toString(),
                        ),
                    )
                    notificationService.show(notification, progress, target)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed Updating", e)
            Result.retry()
        }
    }
    private fun isHourWithinRange(): Boolean {
        val currentTime = LocalTime.now()
        val startTime = LocalTime.of(startTimeToSend, 0)
        val endTime = LocalTime.of(endTimeToSend, 0)
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime)
    }

    private fun goalsUpdatedCheck() : Boolean {
        return sharedPreferences.getBoolean("GoalsUpdated", false)
    }
}
