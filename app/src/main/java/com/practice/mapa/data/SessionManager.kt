package com.practice.mapa.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val usernameKey = stringPreferencesKey("logged_in_username")

    val loggedInUsername: Flow<String?> = context.dataStore.data.map { it[usernameKey] }

    suspend fun getLoggedInUsername(): String? = loggedInUsername.first()

    suspend fun saveSession(username: String) {
        context.dataStore.edit { it[usernameKey] = username }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.remove(usernameKey) }
    }
}
