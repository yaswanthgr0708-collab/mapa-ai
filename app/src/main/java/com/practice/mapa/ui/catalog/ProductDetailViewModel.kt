package com.practice.mapa.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.mapa.data.catalog.CartRepository
import com.practice.mapa.data.catalog.CatalogRepository
import com.practice.mapa.data.catalog.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val catalogRepo: CatalogRepository,
    private val cartRepo: CartRepository
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    // Emits the productId that was just added — Fragment uses it to wire the Undo action.
    private val _addedToCart = MutableSharedFlow<Int>()
    val addedToCart: SharedFlow<Int> = _addedToCart

    fun loadProduct(id: Int) {
        viewModelScope.launch {
            _product.value = catalogRepo.getProductById(id)
        }
    }

    fun addToCart(productId: Int) {
        viewModelScope.launch {
            cartRepo.addToCart(productId)
            _addedToCart.emit(productId)
        }
    }

    fun undoAddToCart(productId: Int) {
        viewModelScope.launch {
            cartRepo.removeOneFromCart(productId)
        }
    }
}
