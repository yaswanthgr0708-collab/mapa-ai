package com.practice.mapa.data.catalog

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practice.mapa.util.PriceUtil

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val description: String,
    val discountPercentage: Int = 0
) {
    // Math.round avoids .99999... truncation when converting stored Double dollars to cents.
    val priceCents: Long get() = Math.round(price * 100)
    val discountedPriceCents: Long get() = PriceUtil.discountedCents(priceCents, discountPercentage)
}
