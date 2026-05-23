package com.practice.mapa.data.catalog

object SeedCatalog {
    private val categories = listOf("Electronics", "Clothing", "Books", "Home")

    private val electronicsNames = mapOf(
        1  to "USB-C Charging Cable",
        2  to "Dual Port Charging Adapter",
        3  to "Foldable Headphones v1",
        4  to "Funky Wired Headphones",
        5  to "Wireless Earbuds",
        6  to "ANC Wireless Earbuds",
        7  to "Gaming Headphones v2",
        8  to "24\" Gaming Monitor S1",
        9  to "27\" Flat LED Gaming Monitor",
        10 to "32\" Pro Gaming Monitor",
        11 to "Home Tower Speaker",
        12 to "Portable Game Console",
        13 to "Compact Desk Speaker",
        14 to "Bluetooth Wireless Mouse",
        15 to "Wireless Optical Mouse",
        16 to "10000mAh Power Bank",
        17 to "15\" Laptop R5",
        18 to "15\" Ultra Laptop R5",
        19 to "13\" Mini Laptop R7",
        20 to "15\" Ultra Laptop R7",
        21 to "Portable Mechanical Keyboard",
        22 to "Smart Home Camera",
        23 to "Wireless Mini Sound Bar",
        24 to "Smart Watch L1",
        25 to "Pocket Sound Bar"
    )

    private val clothingNames = mapOf(
        1  to "Formal Jacket",
        2  to "Men's Casual Shirt",
        3  to "Men's Corduroy Pants",
        4  to "Men's Denim Jeans",
        5  to "Men's Dress Set",
        6  to "Men's Formal Shirt",
        7  to "Men's Polo T-Shirt",
        8  to "Men's Classic Shirt",
        9  to "Unisex Stylish Jacket",
        10 to "Unisex Essential T-Shirt",
        11 to "Unisex White T-Shirts Pack",
        12 to "Women's Kfit Pants",
        13 to "Women's Blazer",
        14 to "Women's Breathable Skirt",
        15 to "Women's Cami Top",
        16 to "Women's Casual Shirt",
        17 to "Women's Denim Jeans",
        18 to "Women's Dress Co-ord Set",
        19 to "Women's Green Leggings",
        20 to "Women's Short Jacket",
        21 to "Women's Loose Fit Shorts",
        22 to "Women's Oversized T-Shirt",
        23 to "Women's Multi-Color Shirt",
        24 to "Women's Sweatshirt",
        25 to "Women's White Dress"
    )

    private val booksNames = mapOf(
        1  to "A Democracy of Image",
        2  to "Bauhaus",
        3  to "Large Format Journals Set",
        4  to "Coffee Table Books Combo",
        5  to "Fashion House: Paris Edition",
        6  to "Fashion Photography",
        7  to "Fervor & Simply Living",
        8  to "Free Style Journal Set",
        9  to "Free Style Journal Vol.1",
        10 to "Great Architecture",
        11 to "La Vengeance",
        12 to "Introduction to Law",
        13 to "Live Beautiful at 100",
        14 to "Luxury Design Book Set",
        15 to "Icon: The Tribute",
        16 to "Real Lives",
        17 to "Sculpture Art Book Set",
        18 to "Simple Dairy",
        19 to "Simple Living",
        20 to "The Bunch",
        21 to "The Fashion",
        22 to "This Is Home",
        23 to "Trouvé",
        24 to "Vintage Collection Books",
        25 to "Your Silence Will Not Protect You"
    )

    private val homeNames = mapOf(
        1  to "Minimalist Wall Frame",
        2  to "Baby Cradle",
        3  to "Small Bar Chair",
        4  to "Decorative Candle Stand",
        5  to "Coffee Table & Chairs Set",
        6  to "Modern Coffee Table",
        7  to "Dust Cleaning Set",
        8  to "Indoor Flower Pot",
        9  to "Kitchen Essentials Set",
        10 to "Wooden Kitchen Set",
        11 to "L-Shaped Sofa Set",
        12 to "Love Seat Sofa",
        13 to "Luxury Coffee Table",
        14 to "Wooden Deck Chairs",
        15 to "Office Chair with Headrest",
        16 to "Ergonomic Push-Back Chair",
        17 to "Outdoor Swing Chair",
        18 to "Outdoor Tub Chairs Set",
        19 to "Simple Office Table",
        20 to "Single Sofa Chair",
        21 to "Sofa Cum Bed",
        22 to "Tea Pot & Cup Set",
        23 to "Utility Mirror",
        24 to "Cup Holder Set",
        25 to "Wooden Crockery Set"
    )

    private fun nameFor(category: String, imageIndex: Int): String {
        val names = when (category) {
            "Electronics" -> electronicsNames
            "Clothing"    -> clothingNames
            "Books"       -> booksNames
            "Home"        -> homeNames
            else          -> emptyMap()
        }
        return names[imageIndex] ?: "$category Product $imageIndex"
    }

    private val electronicsPrices = mapOf(
        1  to 75.0,  2  to 89.0,  3  to 129.0, 4  to 99.0,  5  to 119.0,
        6  to 179.0, 7  to 149.0, 8  to 249.0, 9  to 299.0, 10 to 389.0,
        11 to 199.0, 12 to 229.0, 13 to 149.0, 14 to 89.0,  15 to 79.0,
        16 to 95.0,  17 to 429.0, 18 to 479.0, 19 to 399.0, 20 to 499.0,
        21 to 139.0, 22 to 169.0, 23 to 189.0, 24 to 219.0, 25 to 129.0
    )

    private val clothingPrices = mapOf(
        1  to 69.0,  2  to 35.0,  3  to 55.0,  4  to 59.0,  5  to 72.0,
        6  to 45.0,  7  to 35.0,  8  to 32.0,  9  to 74.0,  10 to 28.0,
        11 to 45.0,  12 to 52.0,  13 to 72.0,  14 to 42.0,  15 to 28.0,
        16 to 32.0,  17 to 58.0,  18 to 74.0,  19 to 38.0,  20 to 68.0,
        21 to 35.0,  22 to 32.0,  23 to 38.0,  24 to 48.0,  25 to 65.0
    )

    private val booksPrices = mapOf(
        1  to 35.0,  2  to 42.0,  3  to 38.0,  4  to 49.0,  5  to 45.0,
        6  to 39.0,  7  to 28.0,  8  to 32.0,  9  to 22.0,  10 to 48.0,
        11 to 19.0,  12 to 45.0,  13 to 35.0,  14 to 50.0,  15 to 42.0,
        16 to 25.0,  17 to 48.0,  18 to 18.0,  19 to 22.0,  20 to 28.0,
        21 to 32.0,  22 to 38.0,  23 to 15.0,  24 to 45.0,  25 to 29.0
    )

    private val homePrices = mapOf(
        1  to 120.0, 2  to 189.0, 3  to 145.0, 4  to 110.0, 5  to 289.0,
        6  to 219.0, 7  to 100.0, 8  to 115.0, 9  to 159.0, 10 to 175.0,
        11 to 299.0, 12 to 275.0, 13 to 289.0, 14 to 245.0, 15 to 229.0,
        16 to 249.0, 17 to 265.0, 18 to 279.0, 19 to 185.0, 20 to 235.0,
        21 to 289.0, 22 to 105.0, 23 to 135.0, 24 to 100.0, 25 to 125.0
    )

    private fun priceFor(category: String, imageIndex: Int): Double {
        val prices = when (category) {
            "Electronics" -> electronicsPrices
            "Clothing"    -> clothingPrices
            "Books"       -> booksPrices
            "Home"        -> homePrices
            else          -> emptyMap()
        }
        return prices[imageIndex] ?: 9.99
    }

    private fun discountFor(id: Int): Int = when {
        id % 7 == 0 -> 40
        id % 10 == 0 || id % 10 == 3 -> 15
        else -> 0
    }

    val products: List<Product> = buildList {
        for (i in 1..100) {
            val category   = categories[(i - 1) / 25]
            val imageIndex = (i - 1) % 25 + 1
            val price      = priceFor(category, imageIndex)
            add(
                Product(
                    id                 = i,
                    name               = nameFor(category, imageIndex),
                    category           = category,
                    price              = price,
                    description        = "${nameFor(category, imageIndex)}. A premium quality item available for immediate purchase.",
                    discountPercentage = discountFor(i),
                    imageIndex         = imageIndex
                )
            )
        }
    }
}
