package com.practice.mapa.data.catalog

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val description: String
)
