package com.github.k409.fitflow.service

import android.content.Context
import android.content.SharedPreferences
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.DrinkReminderState
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.model.NotificationChannel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

private const val NOTIFICATION_IDS_KEY = "notification_ids"

class HydrationNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationService: NotificationService,
    private val sharedPreferences: SharedPreferences,
) {
    fun scheduleNotifications(state: DrinkReminderState) {
        if (state.intakeGoal != 0 && state.cupSize != 0) {
            cancelScheduledNotifications()

            scheduleNotifications(
                intakeGoal = state.intakeGoal,
                cupSize = state.cupSize,
                todayWaterIntake = state.todayWaterIntake,
            )
        }
    }

    private fun scheduleNotifications(
        intakeGoal: Int,
        cupSize: Int,
        todayWaterIntake: Int,
    ) {
        val remainingIntakeGoal = intakeGoal - todayWaterIntake
        val count = remainingIntakeGoal / cupSize

        // TODO migrate to settings and allow user to modify
        val startLocalTime = LocalTime.of(8, 0)
        val endLocalTime = LocalTime.of(21, 0)

        val currentTime = LocalTime.now()

        var notificationTime =
            if (currentTime.isBefore(startLocalTime)) {
                startLocalTime
            } else {
                currentTime
            }

        val remainingHoursToday = Duration.between(notificationTime, endLocalTime)
        val intervalDuration = remainingHoursToday.dividedBy(count.toLong())

        if (notificationTime == currentTime) {
            notificationTime = notificationTime.plus(intervalDuration)
        }

        val scheduledNotificationIds = mutableListOf<Int>()

        repeat(count) { index ->
            val notification = Notification(
                id = index,
                channel = NotificationChannel.HydrationReminder,
                title = context.getString(R.string.hydration_notification_title),
                text = context.getString(
                    R.string.today__progress_liters,
                    "%.1f".format(todayWaterIntake / 1000.0),
                    "%.1f".format(
                        intakeGoal / 1000.0,
                    ),
                ),
            )

            scheduledNotificationIds.add(notification.id)

            notificationService.post(
                notification = notification,
                time = notificationTime,
            )

            println("Scheduled notification at $notificationTime notification: $notification")

            notificationTime = notificationTime.plus(intervalDuration)
        }

        saveScheduledNotificationIds(scheduledNotificationIds)
    }

    private fun cancelScheduledNotifications() {
        val scheduledNotificationIds = getScheduledNotificationIds()

        for (notificationId in scheduledNotificationIds) {
            notificationService.cancel(notificationId)
        }

        saveScheduledNotificationIds(emptyList())
    }

    private fun saveScheduledNotificationIds(
        scheduledNotificationIds: List<Int>,
    ) {
        val notificationIdsJson = Json.encodeToString(scheduledNotificationIds)

        sharedPreferences
            .edit()
            .putString(
                NOTIFICATION_IDS_KEY,
                notificationIdsJson,
            ).apply()
    }

    private fun getScheduledNotificationIds(): MutableList<Int> {
        val ids = mutableListOf<Int>()

        val notificationIdsJson = sharedPreferences
            .getString(
                NOTIFICATION_IDS_KEY,
                null,
            )

        val json = Json { ignoreUnknownKeys = true }

        notificationIdsJson?.let {
            ids.addAll(json.decodeFromString<List<Int>>(it))
        }

        return ids
    }
}
