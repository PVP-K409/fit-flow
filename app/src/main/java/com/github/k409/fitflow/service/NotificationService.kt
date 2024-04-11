package com.github.k409.fitflow.service

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.receiver.NotificationReceiver
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_CHANNEL_ID
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_ID
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_TEXT
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_TITLE
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun post(
        notification: Notification,
        delay: Duration
    ) {
        postNotification(
            notification, System.currentTimeMillis() + delay.toMillis()
        )
    }

    fun post(
        notification: Notification,
        dateTime: LocalDateTime
    ) {
        postNotification(
            notification, dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    fun post(
        notification: Notification,
        time: LocalTime
    ) {
        val dateTime = time.atDate(LocalDate.now())

        postNotification(
            notification, dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    private fun postNotification(
        notification: Notification,
        triggerAtMillis: Long
    ) {
        if (triggerAtMillis <= System.currentTimeMillis()) {
            return
        }

        val intent = Intent(
            context, NotificationReceiver::class.java
        ).apply {
            putExtra(NOTIFICATION_INTENT_ID, notification.id)
            putExtra(NOTIFICATION_INTENT_CHANNEL_ID, notification.channel.channelId)
            putExtra(NOTIFICATION_INTENT_TITLE, notification.title)
            putExtra(NOTIFICATION_INTENT_TEXT, notification.text)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        manager.setExact(
            AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
        )
    }

    fun show(
        notification: Notification
    ) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(
            context, Activity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationAndroid =
            NotificationCompat.Builder(context, notification.channel.channelId)
                .setContentTitle(notification.title)
                .setContentText(notification.text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notification.text))
                .build()

        val manager = NotificationManagerCompat.from(context)

        manager.notify(
            notification.id, notificationAndroid
        )
    }

    fun cancel(
        notification: Notification
    ) {
        cancelNotification(id = notification.id)
    }

    fun cancel(
        id: Int
    ) {
        cancelNotification(id = id)
    }

    private fun cancelNotification(
        id: Int
    ) {
        val intent = Intent(
            context, NotificationReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        pendingIntent?.let {
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(it)
            it.cancel()
        }
    }
}