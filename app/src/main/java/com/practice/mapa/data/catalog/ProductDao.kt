package com.practice.mapa.data.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(products: List<Product>)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    @Query(
        """
        SELECT * FROM products
        WHERE name LIKE '%' || :query || '%'
          AND category IN (:categories)
          AND price >= :minPrice AND price <= :maxPrice
        ORDER BY id ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getProducts(
        query: String,
        categories: List<String>,
        minPrice: Double,
        maxPrice: Double,
        limit: Int,
        offset: Int
    ): List<Product>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): Product?
}
