package com.practice.mapa.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val NIGHT_MODE_KEY    = intPreferencesKey("night_mode")
    private val NOTIF_PROMO_KEY   = booleanPreferencesKey("notif_promo")
    private val NOTIF_ORDERS_KEY  = booleanPreferencesKey("notif_orders")
    private val NOTIF_PRODUCTS_KEY = booleanPreferencesKey("notif_products")

    val nightMode: Flow<Int> = context.settingsDataStore.data.map {
        it[NIGHT_MODE_KEY] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    val notifPromo: Flow<Boolean>    = context.settingsDataStore.data.map { it[NOTIF_PROMO_KEY]   ?: false }
    val notifOrders: Flow<Boolean>   = context.settingsDataStore.data.map { it[NOTIF_ORDERS_KEY]  ?: false }
    val notifProducts: Flow<Boolean> = context.settingsDataStore.data.map { it[NOTIF_PRODUCTS_KEY] ?: false }

    suspend fun setNightMode(mode: Int) {
        context.settingsDataStore.edit { it[NIGHT_MODE_KEY] = mode }
    }

    suspend fun setNotifPromo(enabled: Boolean) {
        context.settingsDataStore.edit { it[NOTIF_PROMO_KEY] = enabled }
    }

    suspend fun setNotifOrders(enabled: Boolean) {
        context.settingsDataStore.edit { it[NOTIF_ORDERS_KEY] = enabled }
    }

    suspend fun setNotifProducts(enabled: Boolean) {
        context.settingsDataStore.edit { it[NOTIF_PRODUCTS_KEY] = enabled }
    }

    suspend fun clearAll() {
        context.settingsDataStore.edit { it.clear() }
    }
}
