package com.github.k409.fitflow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.service.NotificationService
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_CHANNEL_ID
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_ID
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_TEXT
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_TITLE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: NotificationService

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val id = intent.getIntExtra(NOTIFICATION_INTENT_ID, 0)
        val channelId = intent.getStringExtra(NOTIFICATION_INTENT_CHANNEL_ID) ?: ""
        val title = intent.getStringExtra(NOTIFICATION_INTENT_TITLE) ?: ""
        val text = intent.getStringExtra(NOTIFICATION_INTENT_TEXT) ?: ""
        val channel = NotificationChannel.fromChannelId(channelId)
            ?: throw Exception("Invalid notification channel id")

        notificationService.show(
            notification = Notification(
                id = id,
                channel = channel,
                title = title,
                text = text
            )
        )
    }
}