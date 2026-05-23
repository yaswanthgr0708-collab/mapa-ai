package com.practice.mapa.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.mapa.data.SessionManager
import com.practice.mapa.data.catalog.CatalogRepository
import com.practice.mapa.data.catalog.Product
import com.practice.mapa.util.TestMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    val username: StateFlow<String> = sessionManager.loggedInUsername
        .map { it.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val greeting: String = if (TestMode.isEnabled) "Good morning" else {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private val _featuredProducts = MutableStateFlow<List<Product>>(emptyList())
    val featuredProducts: StateFlow<List<Product>> = _featuredProducts

    private val _flashDeals = MutableStateFlow<List<Product>>(emptyList())
    val flashDeals: StateFlow<List<Product>> = _flashDeals

    init {
        viewModelScope.launch {
            _featuredProducts.value = catalogRepository.getFeaturedProducts()
            _flashDeals.value = catalogRepository.getFlashDeals()
        }
    }
}
