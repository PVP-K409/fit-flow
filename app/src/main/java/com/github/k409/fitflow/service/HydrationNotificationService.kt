package com.github.k409.fitflow.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.DrinkReminderState
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.model.NotificationChannel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

private const val NOTIFICATION_IDS_KEY = "notification_ids"
private const val TAG = "HydrationNotificationService"

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
        val count = intakeGoal / cupSize

        if (remainingIntakeGoal <= 0 || count <= 0) {
            return
        }

        val startTime = LocalTime.of(8, 0)
        val endTime = LocalTime.of(22, 0)

        val currentDateTime = LocalDateTime.now()
        val currentTime = currentDateTime.toLocalTime()
        val currentDate = currentDateTime.toLocalDate()

        val interval = Duration.between(startTime, endTime).dividedBy(count.toLong())
        val intervalDuration = Duration.ofMinutes(interval.toMinutes())

        val notificationDateTimes = mutableListOf<LocalDateTime>()

        var notificationTime = if (currentTime < startTime) {
            LocalDateTime.of(currentDate, startTime)
        } else if (currentTime > endTime) {
            LocalDateTime.of(currentDate.plusDays(1), startTime)
        } else {
            if (currentTime + intervalDuration > endTime || currentTime + intervalDuration < startTime
            ) {
                LocalDateTime.of(currentDate.plusDays(1), startTime)
            } else {
                currentDateTime.plus(intervalDuration)
            }
        }

        repeat(count) {
            notificationDateTimes.add(notificationTime)

            val next = notificationTime.plus(intervalDuration)

            notificationTime = if (next.toLocalTime() > endTime || next.toLocalTime() < startTime) {
                LocalDateTime.of(
                    notificationTime.toLocalDate().plusDays(1),
                    startTime,
                )
            } else {
                next
            }
        }

        Log.d(TAG, "Current date time: $currentDateTime")

        val scheduledNotificationIds = notificationDateTimes.mapIndexed { index, dateTime ->
            val notification = Notification(
                id = index,
                channel = NotificationChannel.HydrationReminder,
                title = context.getString(R.string.hydration_notification_title),
                text = context.getString(
                    R.string.today__progress_liters,
                    "%.1f".format(todayWaterIntake / 1000.0),
                    "%.1f".format(intakeGoal / 1000.0),
                ),
            )

            notificationService.post(
                notification = notification,
                dateTime = dateTime,
            )

            Log.d(TAG, "Scheduled notification at $dateTime")

            index
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

        sharedPreferences.edit().putString(
            NOTIFICATION_IDS_KEY,
            notificationIdsJson,
        ).apply()
    }

    private fun getScheduledNotificationIds(): MutableList<Int> {
        val ids = mutableListOf<Int>()

        val notificationIdsJson = sharedPreferences.getString(
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
