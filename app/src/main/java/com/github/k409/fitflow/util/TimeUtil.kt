package com.github.k409.fitflow.util

import java.util.concurrent.TimeUnit

fun formatTime(timeInMillis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
    val milliseconds = timeInMillis % 1000
    return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds)
}