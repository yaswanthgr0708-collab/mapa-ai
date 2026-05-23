package com.practice.mapa.data.catalog

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor(
    private val productDao: ProductDao
) {
    companion object {
        const val PAGE_SIZE = 20
        val ALL_CATEGORIES = listOf("Electronics", "Clothing", "Books", "Home")
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (productDao.count() == 0) {
                productDao.insertAll(SeedCatalog.products)
            }
        }
    }

    suspend fun getProducts(
        query: String = "",
        categories: List<String> = ALL_CATEGORIES,
        minPrice: Double = 0.0,
        maxPrice: Double = 500.0,
        offset: Int = 0
    ): List<Product> = productDao.getProducts(
        query = query,
        categories = categories.ifEmpty { ALL_CATEGORIES },
        minPrice = minPrice,
        maxPrice = maxPrice,
        limit = PAGE_SIZE,
        offset = offset
    )

    suspend fun getProductById(id: Int): Product? = productDao.getProductById(id)

    suspend fun getFeaturedProducts(): List<Product> =
        productDao.getProductsByIds(listOf(1, 14, 27, 40, 53, 66, 79, 92))

    // Flash deals: products where id % 7 == 0 (ids 7,14,21,28,35,42,49,56,63,70,77,84,91,98)
    suspend fun getFlashDeals(): List<Product> =
        productDao.getProductsByIds(listOf(7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84, 91, 98))
}
