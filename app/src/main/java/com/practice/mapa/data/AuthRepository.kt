package com.practice.mapa.data

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {

    /**
     * Validates credentials against [SeedUsers] and applies per-user behaviours
     * that are Appium test-data contracts (Section 5.1 of HANDOFF.md).
     */
    suspend fun login(username: String, password: String): AuthResult {
        val user = SeedUsers.find(username) ?: return AuthResult.InvalidCredentials
        if (user.password != password) return AuthResult.InvalidCredentials

        return when (username) {
            "locked_user" -> AuthResult.AccountLocked
            "slow_user"   -> { delay(3_000); AuthResult.Success(user) }
            "error_user"  -> AuthResult.ServerError
            else          -> AuthResult.Success(user)   // standard_user, empty_user
        }
    }
}
