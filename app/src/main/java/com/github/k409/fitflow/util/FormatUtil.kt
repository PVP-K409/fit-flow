package com.github.k409.fitflow.util

import java.util.Locale
import java.util.concurrent.TimeUnit

fun formatTimeFromSeconds(timeInSeconds: Long): String {
    val hours = TimeUnit.SECONDS.toHours(timeInSeconds)
    val minutes = TimeUnit.SECONDS.toMinutes(timeInSeconds) % 60
    val seconds = timeInSeconds % 60
    return String.format(Locale.US,"%02d:%02d:%02d", hours, minutes, seconds)
}


