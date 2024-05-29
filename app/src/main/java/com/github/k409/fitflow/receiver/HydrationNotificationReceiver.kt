package com.github.k409.fitflow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.service.HydrationNotificationService
import com.github.k409.fitflow.service.NotificationService
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_CHANNEL_ID
import com.github.k409.fitflow.util.NotificationConstants.NOTIFICATION_INTENT_ID
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "HydrationNotificationReceiver"

@AndroidEntryPoint
class HydrationNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var hydrationRepository: HydrationRepository

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Sending notification")

            val state = hydrationRepository.getDrinkReminderState().first()

            val todayWaterIntake = state.todayWaterIntake
            val intakeGoal = state.intakeGoal

            val id = intent.getIntExtra(NOTIFICATION_INTENT_ID, 0)
            val channelId = intent.getStringExtra(NOTIFICATION_INTENT_CHANNEL_ID) ?: ""
            val channel = NotificationChannel.fromChannelId(channelId)
                ?: throw Exception("Invalid notification channel id")

            val title = HydrationNotificationService.getNotificationTitle(context)
            val text = HydrationNotificationService.getNotificationText(
                context = context,
                todayWaterIntake = todayWaterIntake,
                intakeGoal = intakeGoal,
            )

            withContext(Dispatchers.Main) {
                notificationService.show(
                    notification = Notification(
                        id = id,
                        channel = channel,
                        title = title,
                        text = text,
                    ),
                )
            }
        }
    }
}
