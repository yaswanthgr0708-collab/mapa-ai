package com.practice.mapa.ui.forms

import android.content.Context
import androidx.lifecycle.ViewModel
import com.practice.mapa.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class FormsUiState(
    val textValue: String = "",
    val numberValue: String = "",
    val emailValue: String = "",
    val passwordValue: String = "",
    val multilineValue: String = "",
    val dateValue: String = "",
    val timeValue: String = "",
    val countryValue: String = "",
    val radioValue: String = "",
    val checkboxSummary: String = "",
    val switch1: Boolean = false,
    val switch2: Boolean = false,
    val switch3: Boolean = false,
    val seekbarValue: Int = 0,
    // Errors
    val textError: String? = null,
    val numberError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null
)

sealed class FormsEvent {
    data class Submit(
        val textValue: String,
        val numberValue: String,
        val emailValue: String,
        val passwordValue: String,
        val multilineValue: String,
        val dateValue: String,
        val timeValue: String,
        val countryValue: String,
        val radioValue: String,
        val checkboxSummary: String,
        val switch1: Boolean,
        val switch2: Boolean,
        val switch3: Boolean,
        val seekbarValue: Int
    ) : FormsEvent()
}

@HiltViewModel
class FormsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormsUiState())
    val uiState: StateFlow<FormsUiState> = _uiState

    private val _event = MutableStateFlow<FormsEvent?>(null)
    val event: StateFlow<FormsEvent?> = _event

    fun setDate(date: String) = _uiState.update { it.copy(dateValue = date, dateError = null) }
    fun setTime(time: String) = _uiState.update { it.copy(timeValue = time, timeError = null) }
    fun setSeekbar(value: Int) = _uiState.update { it.copy(seekbarValue = value) }
    fun setSwitch1(on: Boolean) = _uiState.update { it.copy(switch1 = on) }
    fun setSwitch2(on: Boolean) = _uiState.update { it.copy(switch2 = on) }
    fun setSwitch3(on: Boolean) = _uiState.update { it.copy(switch3 = on) }

    fun updateCheckboxSummary(a: Boolean, b: Boolean, c: Boolean) {
        val selected = buildList {
            if (a) add("A")
            if (b) add("B")
            if (c) add("C")
        }
        val summary = if (selected.isEmpty()) {
            context.getString(R.string.forms_checkbox_summary_none)
        } else {
            context.getString(R.string.forms_checkbox_summary_format, selected.joinToString(", "))
        }
        _uiState.update { it.copy(checkboxSummary = summary) }
    }

    fun submit(
        text: String, number: String, email: String, password: String,
        multiline: String, country: String, radio: String,
        checkboxA: Boolean, checkboxB: Boolean, checkboxC: Boolean
    ) {
        val textErr     = if (text.isBlank())     context.getString(R.string.forms_error_text_required)     else null
        val numberErr   = if (number.isBlank())   context.getString(R.string.forms_error_number_required)   else null
        val emailErr    = when {
            email.isBlank()                     -> context.getString(R.string.forms_error_email_required)
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                context.getString(R.string.forms_error_email_invalid)
            else -> null
        }
        val passwordErr = if (password.isBlank()) context.getString(R.string.forms_error_password_required) else null
        val dateErr     = if (_uiState.value.dateValue.isBlank()) context.getString(R.string.forms_error_date_required) else null
        val timeErr     = if (_uiState.value.timeValue.isBlank()) context.getString(R.string.forms_error_time_required) else null

        _uiState.update {
            it.copy(
                textError = textErr, numberError = numberErr, emailError = emailErr,
                passwordError = passwordErr, dateError = dateErr, timeError = timeErr
            )
        }

        if (listOf(textErr, numberErr, emailErr, passwordErr, dateErr, timeErr).all { it == null }) {
            updateCheckboxSummary(checkboxA, checkboxB, checkboxC)
            val state = _uiState.value
            _event.value = FormsEvent.Submit(
                textValue = text, numberValue = number, emailValue = email,
                passwordValue = password, multilineValue = multiline,
                dateValue = state.dateValue, timeValue = state.timeValue,
                countryValue = country, radioValue = radio,
                checkboxSummary = state.checkboxSummary,
                switch1 = state.switch1, switch2 = state.switch2, switch3 = state.switch3,
                seekbarValue = state.seekbarValue
            )
        }
    }

    fun consumeEvent() { _event.value = null }
}
