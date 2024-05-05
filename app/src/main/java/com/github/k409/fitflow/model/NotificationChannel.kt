package com.github.k409.fitflow.model

enum class NotificationChannel(
    val channelId: String,
) {
    Default("Default"),
    HydrationReminder("Hydration Reminder"),
    GoalUpdate("Goal Update"),
    WalkingProgress("Walking Progress"),
    ExerciseSession("Exercise Session"),
    AquariumHealth("Aquarium Health"),
    ;

    companion object {
        fun fromChannelId(channelId: String): NotificationChannel? {
            return entries.find {
                it.channelId == channelId
            }
        }
    }
}
