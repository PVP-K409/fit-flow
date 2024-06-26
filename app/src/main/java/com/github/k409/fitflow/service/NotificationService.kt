package com.github.k409.fitflow.service

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.receiver.NotificationReceiver
import com.github.k409.fitflow.ui.MainActivity
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
    @ApplicationContext private val context: Context,
) {

    fun post(
        notification: Notification,
        delay: Duration,
        broadcastReceiver: Class<out BroadcastReceiver> = NotificationReceiver::class.java,
    ) {
        postNotification(
            notification,
            System.currentTimeMillis() + delay.toMillis(),
            broadcastReceiver,
        )
    }

    fun post(
        notification: Notification,
        dateTime: LocalDateTime,
        broadcastReceiver: Class<out BroadcastReceiver> = NotificationReceiver::class.java,
    ) {
        postNotification(
            notification,
            dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            broadcastReceiver,
        )
    }

    fun post(
        notification: Notification,
        time: LocalTime,
        broadcastReceiver: Class<out BroadcastReceiver> = NotificationReceiver::class.java,
    ) {
        val dateTime = time.atDate(LocalDate.now())

        postNotification(
            notification,
            dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            broadcastReceiver,
        )
    }

    private fun postNotification(
        notification: Notification,
        triggerAtMillis: Long,
        broadcastReceiver: Class<out BroadcastReceiver> = NotificationReceiver::class.java,
    ) {
        if (triggerAtMillis <= System.currentTimeMillis()) {
            return
        }

        val intent = Intent(
            context,
            broadcastReceiver,
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
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        manager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent,
        )
    }

    fun show(
        notification: Notification,
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(
            context,
            MainActivity::class.java,
        ).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
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
            notification.id,
            notificationAndroid,
        )
    }

    fun show(
        notification: Notification,
        progress: Int? = null,
        maxProgress: Int? = null,
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val builder = NotificationCompat.Builder(context, notification.channel.channelId)
            .setContentTitle(notification.title)
            .setContentText(notification.text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.text))

        if (progress != null && maxProgress != null) {
            builder.setProgress(maxProgress, progress, false)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        builder.setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(
            notification.id,
            builder.build(),
        )
    }

    fun cancel(
        notification: Notification,
    ) {
        cancelNotification(id = notification.id)
    }

    fun cancel(
        id: Int,
    ) {
        cancelNotification(id = id)
    }

    private fun cancelNotification(
        id: Int,
    ) {
        val intent = Intent(
            context,
            NotificationReceiver::class.java,
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )

        pendingIntent?.let {
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(it)
            it.cancel()
        }
    }

    fun createNotificationChannel(
        channelName: String,
        channelId: String,
        importance: Int,
        setShowBadge: Boolean = false,
        enableVibration: Boolean = false,
        enableLights: Boolean = false,
        vibrationPattern: LongArray = longArrayOf(0L),
    ): NotificationChannel {
        return NotificationChannel(
            channelId,
            channelName,
            importance,
        ).apply {
            this.setShowBadge(setShowBadge)
            this.enableVibration(enableVibration)
            this.enableLights(enableLights)
            this.vibrationPattern = vibrationPattern
        }
    }

    fun createNotification(
        notificationTitle: String,
        notificationText: String,
        notificationChannel: String,
        setSmallIcon: Int = R.drawable.ic_launcher_foreground,
        setPriority: Int = NotificationCompat.PRIORITY_LOW,
        setAutoCancel: Boolean = true,
    ): android.app.Notification {
        return NotificationCompat.Builder(context, notificationChannel)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(setSmallIcon)
            .setPriority(setPriority)
            .setAutoCancel(setAutoCancel)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .build()
    }
}
