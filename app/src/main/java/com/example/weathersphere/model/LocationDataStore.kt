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

    suspend fun setLocationSelected(selected: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOCATION_SELECTED] = selected
        }
    }

    val isNotificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION] ?: true
        }

    suspend fun setNotificationEnabled(selected: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION] = selected
        }
    }

    companion object {
        private val KEY_LOCATION_SELECTED = booleanPreferencesKey("location_selected")
        private val NOTIFICATION = booleanPreferencesKey("notification_enabled")
    }
}