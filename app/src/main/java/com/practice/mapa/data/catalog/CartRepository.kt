package com.practice.mapa.data.catalog

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    val cartItems: Flow<List<CartItemWithProduct>> = cartDao.observeCart()
    val cartItemCount: Flow<Int> = cartDao.observeCartItemCount()

    suspend fun addToCart(productId: Int) {
        val existing = cartDao.getCartItem(productId)
        if (existing == null) {
            cartDao.upsert(CartItem(productId = productId, quantity = 1))
        } else {
            cartDao.updateQuantity(productId, existing.quantity + 1)
        }
    }

    suspend fun increaseQuantity(productId: Int) {
        val existing = cartDao.getCartItem(productId) ?: return
        cartDao.updateQuantity(productId, existing.quantity + 1)
    }

    suspend fun decreaseQuantity(productId: Int) {
        val existing = cartDao.getCartItem(productId) ?: return
        if (existing.quantity > 1) {
            cartDao.updateQuantity(productId, existing.quantity - 1)
        }
    }

    suspend fun removeItem(productId: Int) = cartDao.removeItem(productId)

    /** Decrements by 1; removes entirely if quantity was 1. Used for Undo after Add-to-cart. */
    suspend fun removeOneFromCart(productId: Int) {
        val existing = cartDao.getCartItem(productId) ?: return
        if (existing.quantity > 1) {
            cartDao.updateQuantity(productId, existing.quantity - 1)
        } else {
            cartDao.removeItem(productId)
        }
    }

    suspend fun restoreItem(item: CartItem) = cartDao.upsert(item)

    suspend fun clearCart() = cartDao.clearCart()
}
