package com.practice.mapa.data.catalog

object SeedCatalog {
    private val categories = listOf("Electronics", "Clothing", "Books", "Home")

    val products: List<Product> = buildList {
        for (i in 1..100) {
            val category = categories[(i - 1) / 25]
            val price = 5.0 + (i - 1) * 4.94
            add(
                Product(
                    id = i,
                    name = "$category Product $i",
                    category = category,
                    price = price,
                    description = "$category product #$i. A premium quality item available for immediate purchase."
                )
            )
        }
    }
}
