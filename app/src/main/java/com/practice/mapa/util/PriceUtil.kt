package com.practice.mapa.util

import java.util.Locale

object PriceUtil {

    fun discountedCents(originalCents: Long, discountPercentage: Int): Long =
        originalCents * (100 - discountPercentage) / 100

    fun savingsCents(originalCents: Long, discountPercentage: Int): Long =
        originalCents - discountedCents(originalCents, discountPercentage)

    fun formatCents(cents: Long): String =
        String.format(Locale.US, "$%.2f", cents / 100.0)
}
