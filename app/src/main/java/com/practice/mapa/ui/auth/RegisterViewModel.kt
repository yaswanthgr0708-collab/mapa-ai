package com.practice.mapa.ui.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        dob: String,
        termsAccepted: Boolean
    ) {
        val nameBlank     = name.isBlank()
        val emailBlank    = email.isBlank()
        val passwordBlank = password.isBlank()
        val dobBlank      = dob.isBlank()

        if (nameBlank || emailBlank || passwordBlank || dobBlank) {
            _uiState.value = RegisterUiState.FieldErrors(
                nameError     = nameBlank,
                emailError    = emailBlank,
                passwordError = passwordBlank,
                dobError      = dobBlank
            )
            return
        }

        if (password != confirmPassword) {
            _uiState.value = RegisterUiState.PasswordMismatch
            return
        }

        if (!termsAccepted) {
            _uiState.value = RegisterUiState.TermsError
            return
        }

        // In Phase 2, registration is in-memory only: we simply signal success.
        _uiState.value = RegisterUiState.Success
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Success : RegisterUiState()
    data class FieldErrors(
        val nameError: Boolean     = false,
        val emailError: Boolean    = false,
        val passwordError: Boolean = false,
        val dobError: Boolean      = false
    ) : RegisterUiState()
    object PasswordMismatch : RegisterUiState()
    object TermsError : RegisterUiState()
}
