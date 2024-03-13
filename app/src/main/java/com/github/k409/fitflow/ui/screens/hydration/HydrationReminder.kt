package com.github.k409.fitflow.ui.screens.hydration

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.k409.fitflow.R
import java.util.Calendar

class HydrationReminder : BroadcastReceiver() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "WaterReminderChannel"
        private const val NOTIFICATION_ID = 1001
        private var lastNotificationTime = 0L
    }

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        context?.let {
            showWaterNotification(context)
        }
    }

    fun scheduleWaterReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationReminder::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.HOUR, 3)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HOUR * 3,
            pendingIntent,
        )
    }

    private fun showWaterNotification(context: Context) {
        val currentTime = Calendar.getInstance()
        val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)

        if (hourOfDay in 8..20) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.hydration_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.hydration_notification_channel_description)
            }
            notificationManager.createNotificationChannel(channel)

            val builder = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.hydration_notification_title))
                .setContentText(context.getString(R.string.hydration_notification_text))
                .setSmallIcon(R.drawable.primary_fish)
                .setAutoCancel(true)

            notificationManager.notify(NOTIFICATION_ID, builder.build())

            lastNotificationTime = System.currentTimeMillis()
        }
    }
}
