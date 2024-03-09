package com.github.k409.fitflow.ui.screens.waterLogging

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.github.k409.fitflow.R
import java.util.Calendar

class WaterReminder : BroadcastReceiver() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "WaterReminderChannel"
        private const val NOTIFICATION_ID = 1001
        private var lastNotificationTime = 0L
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            showWaterNotification(context)
        }
    }

    fun scheduleWaterReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WaterReminder::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.HOUR, 3) // Set the initial alarm 1 minute later
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HOUR * 3,
            pendingIntent
        )
    }

    private fun showWaterNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Drink some water",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminder to stay hydrated"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Don't forget to stay hydrated!")
            .setContentText("It's been 3 hour since you last drank water," +
                    " don't forget to stay hydrated")
            .setSmallIcon(R.drawable.primary_fish)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())

        // Update lastNotificationTime after showing the notification
        lastNotificationTime = System.currentTimeMillis()
    }
}
