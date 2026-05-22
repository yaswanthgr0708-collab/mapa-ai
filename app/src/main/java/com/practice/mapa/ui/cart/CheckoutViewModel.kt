package com.practice.mapa.ui.cart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.mapa.R
import com.practice.mapa.data.catalog.CartRepository
import com.practice.mapa.data.catalog.OrderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class CheckoutStep { ADDRESS, PAYMENT, REVIEW }

data class CheckoutUiState(
    val step: CheckoutStep = CheckoutStep.ADDRESS,
    // Address
    val name: String = "",
    val street: String = "",
    val city: String = "",
    val zip: String = "",
    val country: String = "United States",
    // Payment
    val cardNumber: String = "",
    val expiry: String = "",
    val cvv: String = "",
    // Errors
    val nameError: String? = null,
    val streetError: String? = null,
    val cityError: String? = null,
    val zipError: String? = null,
    val cardError: String? = null,
    val expiryError: String? = null,
    val cvvError: String? = null,
    // Order total
    val orderTotal: Double = 0.0
) {
    val maskedCard: String get() {
        val digits = cardNumber.filter { it.isDigit() }
        val last4 = digits.takeLast(4).padStart(4, '0')
        return "•••• •••• •••• $last4"
    }
}

sealed class CheckoutEvent {
    data class OrderPlaced(val orderId: String) : CheckoutEvent()
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cartRepo: CartRepository,
    private val orderManager: OrderManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState

    private val _event = MutableStateFlow<CheckoutEvent?>(null)
    val event: StateFlow<CheckoutEvent?> = _event

    fun setOrderTotal(total: Double) = _uiState.update { it.copy(orderTotal = total) }

    fun updateAddress(name: String, street: String, city: String, zip: String, country: String) {
        _uiState.update { it.copy(name = name, street = street, city = city, zip = zip, country = country) }
    }

    fun updatePayment(card: String, expiry: String, cvv: String) {
        _uiState.update { it.copy(cardNumber = card, expiry = expiry, cvv = cvv) }
    }

    fun nextStep(name: String, street: String, city: String, zip: String, country: String) {
        updateAddress(name, street, city, zip, country)
        val nameErr   = if (name.isBlank())   context.getString(R.string.checkout_error_name_required)   else null
        val streetErr = if (street.isBlank()) context.getString(R.string.checkout_error_street_required) else null
        val cityErr   = if (city.isBlank())   context.getString(R.string.checkout_error_city_required)   else null
        val zipErr    = if (zip.isBlank())    context.getString(R.string.checkout_error_zip_required)    else null
        _uiState.update { it.copy(nameError = nameErr, streetError = streetErr, cityError = cityErr, zipError = zipErr) }
        if (listOf(nameErr, streetErr, cityErr, zipErr).all { it == null }) {
            _uiState.update { it.copy(step = CheckoutStep.PAYMENT) }
        }
    }

    fun nextStepPayment(card: String, expiry: String, cvv: String) {
        updatePayment(card, expiry, cvv)
        val cardErr   = validateCard(card)
        val expiryErr = validateExpiry(expiry)
        val cvvErr    = validateCvv(cvv)
        _uiState.update { it.copy(cardError = cardErr, expiryError = expiryErr, cvvError = cvvErr) }
        if (listOf(cardErr, expiryErr, cvvErr).all { it == null }) {
            _uiState.update { it.copy(step = CheckoutStep.REVIEW) }
        }
    }

    private fun validateCard(card: String): String? = when {
        card.isBlank()                          -> context.getString(R.string.checkout_error_card_required)
        card.filter { it.isDigit() }.length != 16 -> context.getString(R.string.checkout_error_card_digits)
        else                                    -> null
    }

    private fun validateExpiry(expiry: String): String? {
        if (expiry.isBlank()) return context.getString(R.string.checkout_error_expiry_required)
        if (!expiry.matches(Regex("""\d{2}/\d{2}"""))) return context.getString(R.string.checkout_error_expiry_format)

        val month = expiry.substring(0, 2).toInt()
        if (month < 1 || month > 12) return context.getString(R.string.checkout_error_expiry_format)

        val entryYear = 2000 + expiry.substring(3).toInt()
        val cal = Calendar.getInstance()
        val currentYear  = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH) + 1  // Calendar months are 0-based

        if (entryYear < currentYear) return context.getString(R.string.checkout_error_expiry_expired)
        if (entryYear == currentYear && month < currentMonth) return context.getString(R.string.checkout_error_expiry_expired)

        return null
    }

    private fun validateCvv(cvv: String): String? = when {
        cvv.isBlank()                               -> context.getString(R.string.checkout_error_cvv_required)
        cvv.length != 3 || !cvv.all { it.isDigit() } -> context.getString(R.string.checkout_error_cvv_digits)
        else                                        -> null
    }

    fun goBack() {
        _uiState.update {
            when (it.step) {
                CheckoutStep.PAYMENT -> it.copy(step = CheckoutStep.ADDRESS)
                CheckoutStep.REVIEW  -> it.copy(step = CheckoutStep.PAYMENT)
                else                 -> it
            }
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            val orderId = orderManager.nextOrderId()
            cartRepo.clearCart()
            _event.value = CheckoutEvent.OrderPlaced(orderId)
        }
    }

    fun consumeEvent() { _event.value = null }
}
