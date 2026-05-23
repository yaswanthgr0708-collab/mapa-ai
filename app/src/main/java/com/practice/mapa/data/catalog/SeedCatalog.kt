package com.practice.mapa.data.catalog

object SeedCatalog {
    private val categories = listOf("Electronics", "Clothing", "Books", "Home")

    private fun discountFor(id: Int): Int = when {
        id % 7 == 0 -> 40
        id % 10 == 0 || id % 10 == 3 -> 15
        else -> 0
    }

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
                    description = "$category product #$i. A premium quality item available for immediate purchase.",
                    discountPercentage = discountFor(i)
                )
            )
        }
    }
}
