package com.github.k409.fitflow.model

enum class NotificationChannel(
    val channelId: String
) {
    HydrationReminder("Hydration Reminder");

    companion object {
        fun fromChannelId(channelId: String): NotificationChannel? {
            return entries.find {
                it.channelId == channelId
            }
        }
    }
}