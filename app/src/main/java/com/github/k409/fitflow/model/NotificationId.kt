package com.github.k409.fitflow.model

enum class NotificationId(
    val notificationId: Int,
) {
    GoalUpdate(1),
    WalkingProgress(2),
    ExerciseSession(3),
    AquariumHealth(4),
}
