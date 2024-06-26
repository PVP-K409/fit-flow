package com.github.k409.fitflow.data.preferences

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val THEME_TYPE = stringPreferencesKey("theme_type")
    val THEME = stringPreferencesKey("theme")

    val CUP_SIZE = intPreferencesKey("cup_size")

    // TODO: Implement in future
    val NOTICATION_INTERVAL_START = stringPreferencesKey("notification_interval_start")
    val NOTICATION_INTERVAL_END = stringPreferencesKey("notification_interval_end")

    val FCM_TOKEN = stringPreferencesKey("fcm_token")

    val YESTERDAY = stringPreferencesKey("yesterday")

    val WATER_LEVEL = floatPreferencesKey("water_level")
    val HEALTH_LEVEL = floatPreferencesKey("health_level")
}
