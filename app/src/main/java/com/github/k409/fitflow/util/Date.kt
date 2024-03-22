package com.github.k409.fitflow.util

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalStdlibApi::class)
fun getShortWeekdayNames(): List<String> {
    return DayOfWeek.entries.map {
        it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }
}