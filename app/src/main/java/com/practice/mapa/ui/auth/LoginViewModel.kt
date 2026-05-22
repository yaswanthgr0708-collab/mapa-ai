package com.practice.mapa.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.mapa.data.AuthRepository
import com.practice.mapa.data.AuthResult
import com.practice.mapa.data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        // Auto-navigate to Home if a session already exists (app restart or back-nav after logout won't hit this).
        viewModelScope.launch {
            val username = sessionManager.getLoggedInUsername()
            if (!username.isNullOrBlank()) {
                _uiState.value = LoginUiState.NavigateToHome(username)
            }
        }
    }

    fun login(username: String, password: String) {
        val usernameBlank = username.isBlank()
        val passwordBlank = password.isBlank()
        if (usernameBlank || passwordBlank) {
            _uiState.value = LoginUiState.FieldErrors(
                showUsernameError = usernameBlank,
                showPasswordError = passwordBlank
            )
            return
        }

        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = authRepository.login(username.trim(), password)) {
                is AuthResult.Success -> {
                    sessionManager.saveSession(result.user.username)
                    LoginUiState.NavigateToHome(result.user.username)
                }
                AuthResult.InvalidCredentials -> LoginUiState.InvalidCredentials
                AuthResult.AccountLocked      -> LoginUiState.AccountLocked
                AuthResult.ServerError        -> LoginUiState.ServerError
                AuthResult.Empty              -> {
                    // empty_user logs in normally; the "empty" behaviour is in Phase 3 data layer.
                    sessionManager.saveSession("empty_user")
                    LoginUiState.NavigateToHome("empty_user")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class NavigateToHome(val username: String) : LoginUiState()
    data class FieldErrors(
        val showUsernameError: Boolean = false,
        val showPasswordError: Boolean = false
    ) : LoginUiState()
    object InvalidCredentials : LoginUiState()
    object AccountLocked : LoginUiState()
    object ServerError : LoginUiState()
}
