package com.practice.mapa.data.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Transaction
    @Query("SELECT * FROM cart_items ORDER BY productId ASC")
    fun observeCart(): Flow<List<CartItemWithProduct>>

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM cart_items")
    fun observeCartItemCount(): Flow<Int>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItem(productId: Int): CartItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItem)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: Int, quantity: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun removeItem(productId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
