package com.practice.mapa.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.mapa.data.SettingsManager
import com.practice.mapa.data.SessionManager
import com.practice.mapa.data.catalog.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val sessionManager: SessionManager,
    private val cartRepository: CartRepository
) : ViewModel() {

    val nightMode: StateFlow<Int> = settingsManager.nightMode.stateIn(
        viewModelScope, SharingStarted.Eagerly, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )
    val notifPromo: StateFlow<Boolean> = settingsManager.notifPromo.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )
    val notifOrders: StateFlow<Boolean> = settingsManager.notifOrders.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )
    val notifProducts: StateFlow<Boolean> = settingsManager.notifProducts.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin

    fun setNightMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        viewModelScope.launch { settingsManager.setNightMode(mode) }
    }

    fun setNotifPromo(enabled: Boolean)    { viewModelScope.launch { settingsManager.setNotifPromo(enabled) } }
    fun setNotifOrders(enabled: Boolean)   { viewModelScope.launch { settingsManager.setNotifOrders(enabled) } }
    fun setNotifProducts(enabled: Boolean) { viewModelScope.launch { settingsManager.setNotifProducts(enabled) } }

    fun clearData() {
        viewModelScope.launch {
            cartRepository.clearCart()
            sessionManager.clearSession()
            settingsManager.clearAll()
            _navigateToLogin.value = true
        }
    }

    fun onNavigatedToLogin() { _navigateToLogin.value = false }
}
