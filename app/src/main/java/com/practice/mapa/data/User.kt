package com.practice.mapa.data

data class User(
    val username: String,
    val password: String,
    val fullName: String = username
)
