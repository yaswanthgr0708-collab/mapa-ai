package com.practice.mapa.data.catalog

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Product::class, CartItem::class],
    version = 1,
    exportSchema = false
)
abstract class MapaDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
}
