package com.github.k409.fitflow.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.github.k409.fitflow.model.theme.Theme
import com.github.k409.fitflow.model.theme.ThemePreferences
import com.github.k409.fitflow.model.theme.ThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


const val DATASTORE_NAME = "preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)

class PreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    suspend fun <T> putPreference(
        key: Preferences.Key<T>,
        value: T
    ) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun <T> getPreference(
        key: Preferences.Key<T>,
        default: T
    ): Flow<T> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: default
        }
    }

    suspend fun updateThemePreferences(themePreferences: ThemePreferences) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_TYPE] = themePreferences.themeType.name
            preferences[PreferenceKeys.THEME] = themePreferences.theme.name
        }
    }

    val themePreferences: Flow<ThemePreferences> =
        dataStore.data.map { preferences ->
            ThemePreferences(
                themeType = ThemeType.valueOf(
                    preferences[PreferenceKeys.THEME_TYPE]
                        ?: ThemeType.AUTOMATIC.name
                ),
                theme = Theme.valueOf(
                    preferences[PreferenceKeys.THEME] ?: Theme.GREEN.name
                )
            )
        }

}
