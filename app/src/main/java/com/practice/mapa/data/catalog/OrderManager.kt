package com.practice.mapa.data.catalog

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.orderDataStore: DataStore<Preferences> by preferencesDataStore(name = "orders")

@Singleton
class OrderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val counterKey = intPreferencesKey("order_counter")

    suspend fun nextOrderId(): String {
        var newCounter = 1
        context.orderDataStore.edit { prefs ->
            newCounter = (prefs[counterKey] ?: 0) + 1
            prefs[counterKey] = newCounter
        }
        return "ORD-%06d".format(newCounter)
    }
}
