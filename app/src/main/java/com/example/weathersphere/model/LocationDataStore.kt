package com.example.weathersphere.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LocationDataStore(private val context: Context) {

    val locationSelected: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_LOCATION_SELECTED] ?: false
        }

    suspend fun setLocationSelected(isSelected: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOCATION_SELECTED] = isSelected
        }
    }

    val isNotificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION] ?: true
        }

    suspend fun setNotificationEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION] = isEnabled
        }
    }

    val getLocation: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LOCATION] ?: ""
        }

    suspend fun setLocation(location: String) {
        context.dataStore.edit { preferences ->
            preferences[LOCATION] = location
        }
    }

    val getWindSpeed: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[WIND_SPEED] ?: ""
        }

    suspend fun setWindSpeed(windSpeed: String) {
        context.dataStore.edit { preferences ->
            preferences[WIND_SPEED] = windSpeed
        }
    }

    val getLanguage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE] ?: ""
        }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    val getTemperature: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[TEMPERATURE] ?: ""
        }

    suspend fun setTemperature(temperature: String) {
        context.dataStore.edit { preferences ->
            preferences[TEMPERATURE] = temperature
        }
    }

    companion object {
        private val KEY_LOCATION_SELECTED = booleanPreferencesKey("location_selected")
        private val NOTIFICATION = booleanPreferencesKey("notification_enabled")
        private val LOCATION = stringPreferencesKey("location")
        private val WIND_SPEED = stringPreferencesKey("wind")
        private val LANGUAGE = stringPreferencesKey("language")
        private val TEMPERATURE = stringPreferencesKey("temperature")
    }
}