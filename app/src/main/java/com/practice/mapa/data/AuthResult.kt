package com.practice.mapa.data

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    object InvalidCredentials : AuthResult()
    object AccountLocked : AuthResult()
    object ServerError : AuthResult()
    object Empty : AuthResult()
}
