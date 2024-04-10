package com.github.k409.fitflow.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.k409.fitflow.R
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.service.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalTime

private const val NOTIFICATION_IDS_KEY = "notification_ids"

@HiltWorker
class DrinkReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationService: NotificationService,
    private val sharedPreferences: SharedPreferences,
    private val hydrationRepository: HydrationRepository,
) : CoroutineWorker(
    context,
    workerParams
) {

    companion object {
        const val WORKER_NAME = "com.github.k409.fitflow.worker.DrinkReminderWorker"
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private var scheduledNotificationIds = mutableListOf<Int>()

    init {
        scheduledNotificationIds = getScheduledNotificationIds()
    }

    override suspend fun doWork(): Result {
        initializeReminderListener()

        return Result.success()
    }

    private suspend fun initializeReminderListener(): Result {
        val stateFlow =
            combine(
                hydrationRepository.getWaterIntakeGoal(),
                hydrationRepository.getCupSize(),
                hydrationRepository.getTodayWaterIntake(),
            ) { goal, cupSize, record ->
                DrinkReminderState(
                    cupSize = cupSize,
                    intakeGoal = goal,
                    todayWaterIntake = record.waterIntake
                )
            }
                .stateIn(
                    scope = scope,
                    started = SharingStarted.Eagerly,
                    initialValue = DrinkReminderState()
                )

        stateFlow.collect { state ->
            if (state.intakeGoal != 0 && state.cupSize != 0) {
                cancelScheduledNotifications()

                scheduleNotifications(
                    intakeGoal = state.intakeGoal,
                    cupSize = state.cupSize,
                    todayWaterIntake = state.todayWaterIntake
                )
            }
        }
    }

    private fun scheduleNotifications(
        intakeGoal: Int,
        cupSize: Int,
        todayWaterIntake: Int
    ) {
        val remainingIntakeGoal = intakeGoal - todayWaterIntake
        val count = remainingIntakeGoal / cupSize
        // TODO: move 8 and 21 to preferences
        val startHour = 8
        val endHour = 21
        val totalDuration = Duration.ofHours((endHour - startHour).toLong())
        val intervalDuration = totalDuration.dividedBy(count.toLong())
        var notificationTime = LocalTime.of(startHour, 0)

        repeat(count) {
            val notification = Notification(
                channel = NotificationChannel.HydrationReminder,
                title = context.getString(R.string.hydration_notification_title),
                text = context.getString(R.string.hydration_notification_text) + ". " +
                        context.getString(
                            R.string.today__progress_liters,
                            "%.1f".format(todayWaterIntake / 1000.0),
                            "%.1f".format(
                                intakeGoal / 1000.0
                            )
                        )
            )

            scheduledNotificationIds.add(notification.id)

            notificationService.post(
                notification = notification,
                time = notificationTime
            )

            notificationTime = notificationTime.plus(intervalDuration)
        }

        saveScheduledNotificationIds()
    }

    private fun cancelScheduledNotifications() {
        for (notificationId in scheduledNotificationIds) {
            notificationService.cancel(notificationId)
        }

        scheduledNotificationIds.clear()
        saveScheduledNotificationIds()
    }

    private fun saveScheduledNotificationIds() {
        val notificationIdsJson = Json.encodeToString(scheduledNotificationIds)

        sharedPreferences
            .edit()
            .putString(
                NOTIFICATION_IDS_KEY,
                notificationIdsJson
            ).apply()
    }

    private fun getScheduledNotificationIds(): MutableList<Int> {
        val ids = mutableListOf<Int>()

        val notificationIdsJson = sharedPreferences
            .getString(
                NOTIFICATION_IDS_KEY,
                null
            )

        val json = Json { ignoreUnknownKeys = true }

        notificationIdsJson?.let {
            ids.addAll(json.decodeFromString<List<Int>>(it))
        }

        return ids
    }
}

data class DrinkReminderState(
    val cupSize: Int = 0,
    val intakeGoal: Int = 0,
    val todayWaterIntake: Int = 0
)