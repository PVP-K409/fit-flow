package com.github.k409.fitflow.service

import android.util.Log
import com.github.k409.fitflow.data.preferences.PreferenceKeys
import com.github.k409.fitflow.data.preferences.PreferencesRepository
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.model.NotificationChannel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FirebaseCloudMessagingService"

@AndroidEntryPoint
class FirebaseCloudMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + job)

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")

        saveToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val remoteNotification = remoteMessage.notification ?: return

        val notification = Notification(
            channel = NotificationChannel.Default,
            title = remoteNotification.title ?: "",
            text = remoteNotification.body ?: "",
        )

        notificationService.show(notification)
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }

    private fun saveToken(token: String?) {
        if (token == null) {
            return
        }

        coroutineScope.launch {
            preferencesRepository.putPreference(
                key = PreferenceKeys.FCM_TOKEN,
                value = token,
            )
        }
    }
}
