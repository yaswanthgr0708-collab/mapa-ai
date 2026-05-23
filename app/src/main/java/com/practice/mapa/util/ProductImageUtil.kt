package com.practice.mapa.util

import com.practice.mapa.R

object ProductImageUtil {

    fun imageResFor(category: String): Int = when (category) {
        "Electronics" -> R.drawable.ic_category_electronics
        "Clothing"    -> R.drawable.ic_category_clothing
        "Books"       -> R.drawable.ic_category_books
        else          -> R.drawable.ic_category_home
    }

    // Pastel tint matching each category's primary color
    fun backgroundColorFor(category: String): Int = when (category) {
        "Electronics" -> 0xFFE3F2FD.toInt()   // light blue
        "Clothing"    -> 0xFFFCE4EC.toInt()   // light pink
        "Books"       -> 0xFFEFEBE9.toInt()   // light brown
        else          -> 0xFFE8F5E9.toInt()   // light green
    }
}
