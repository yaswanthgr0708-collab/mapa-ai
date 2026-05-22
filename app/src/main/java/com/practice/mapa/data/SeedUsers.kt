package com.practice.mapa.data

/**
 * Hard-coded user table. Values are test-data contracts — do not change usernames,
 * passwords, or behaviours without updating the Appium test suite.
 */
object SeedUsers {

    val all = listOf(
        User("standard_user", "password123", "Standard User"),
        User("locked_user",   "password123", "Locked User"),
        User("slow_user",     "password123", "Slow User"),
        User("error_user",    "password123", "Error User"),
        User("empty_user",    "password123", "Empty User"),
    )

    fun find(username: String): User? = all.find { it.username == username }
}
