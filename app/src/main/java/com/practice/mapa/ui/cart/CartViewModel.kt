package com.practice.mapa.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.mapa.data.catalog.CartItemWithProduct
import com.practice.mapa.data.catalog.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.practice.mapa.util.PriceUtil
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepo: CartRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartItemWithProduct>> = cartRepo.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalSavings: StateFlow<Long> = cartRepo.cartItems
        .map { items ->
            items.sumOf { item ->
                PriceUtil.savingsCents(item.product.priceCents, item.product.discountPercentage) *
                    item.cartItem.quantity
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    fun increaseQuantity(productId: Int) {
        viewModelScope.launch { cartRepo.increaseQuantity(productId) }
    }

    fun decreaseQuantity(productId: Int) {
        viewModelScope.launch { cartRepo.decreaseQuantity(productId) }
    }

    fun removeItem(productId: Int) {
        viewModelScope.launch { cartRepo.removeItem(productId) }
    }

    fun restoreItem(item: com.practice.mapa.data.catalog.CartItemWithProduct) {
        viewModelScope.launch { cartRepo.restoreItem(item.cartItem) }
    }
}
