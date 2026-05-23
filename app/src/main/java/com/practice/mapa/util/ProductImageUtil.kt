package com.practice.mapa.util

import android.content.Context
import com.practice.mapa.R

object ProductImageUtil {

    fun getProductImageRes(context: Context, category: String, imageIndex: Int): Int {
        val cat = category.lowercase()
        val idx = imageIndex.toString().padStart(2, '0')
        val resourceName = "product_${cat}_${idx}"
        val resId = context.resources.getIdentifier(
            resourceName, "drawable", context.packageName
        )
        return if (resId != 0) resId else fallbackRes(category)
    }

    fun fallbackRes(category: String): Int = when (category.lowercase()) {
        "electronics" -> R.drawable.ic_category_electronics
        "clothing"    -> R.drawable.ic_category_clothing
        "books"       -> R.drawable.ic_category_books
        "home"        -> R.drawable.ic_category_home
        else          -> R.drawable.ic_category_electronics
    }

    // Pastel tint used as background behind fallback vector icons and image placeholders.
    fun backgroundColorFor(category: String): Int = when (category) {
        "Electronics" -> 0xFFE3F2FD.toInt()
        "Clothing"    -> 0xFFFCE4EC.toInt()
        "Books"       -> 0xFFEFEBE9.toInt()
        else          -> 0xFFE8F5E9.toInt()
    }
}
