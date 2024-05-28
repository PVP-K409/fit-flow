package com.github.k409.fitflow.model

data class Notification(
    var id: Int = -1,
    val channel: NotificationChannel,
    val title: String = "",
    val text: String = "",
) {
    init {
        if (id == -1) {
            id = hashCode()
        }
    }
}
