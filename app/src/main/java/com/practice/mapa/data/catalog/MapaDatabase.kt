package com.practice.mapa.data.catalog

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Product::class, CartItem::class],
    version = 2,
    exportSchema = false
)
abstract class MapaDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE products ADD COLUMN discountPercentage INTEGER NOT NULL DEFAULT 0"
                )
                // Backfill discounts for existing rows using the same deterministic formula
                // used by SeedCatalog for fresh installs.
                db.execSQL(
                    """
                    UPDATE products SET discountPercentage =
                        CASE
                            WHEN id % 7 = 0 THEN 40
                            WHEN id % 10 = 0 OR id % 10 = 3 THEN 15
                            ELSE 0
                        END
                    """.trimIndent()
                )
            }
        }
    }
}
