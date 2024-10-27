package no.uio.ifi.titanic

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class DataStoreViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore
    private val darkModeKey = booleanPreferencesKey("dark_mode")

    val darkModeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[darkModeKey] ?: false
        }
        .catch { emit(false) }

    suspend fun toggleDarkMode() {
        dataStore.edit { preferences ->
            val currentSetting = preferences[darkModeKey] ?: false
            preferences[darkModeKey] = !currentSetting
        }
    }
}