package com.practice.mapa.ui.cart

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentCheckoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CheckoutViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderTotal = (arguments?.getFloat("orderTotal") ?: 0f).toDouble()
        viewModel.setOrderTotal(orderTotal)

        val countries = resources.getStringArray(R.array.countries)
        val countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.checkoutSpinnerCountry.adapter = countryAdapter

        // Auto-format expiry as MM/YY while the user types digits only.
        binding.checkoutInputExpiry.addTextChangedListener(ExpiryTextWatcher())

        binding.checkoutButtonBack.setOnClickListener { handleBack() }
        binding.checkoutButtonNext.setOnClickListener { handleNext() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                renderStep(state)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.event.collect { event ->
                if (event is CheckoutEvent.OrderPlaced) {
                    viewModel.consumeEvent()
                    findNavController().navigate(
                        R.id.action_checkoutFragment_to_orderConfirmationFragment,
                        bundleOf(
                            "orderId"    to event.orderId,
                            "orderTotal" to viewModel.uiState.value.orderTotal.toFloat()
                        )
                    )
                }
            }
        }
    }

    private fun handleBack() {
        if (viewModel.uiState.value.step == CheckoutStep.ADDRESS) {
            findNavController().popBackStack()
        } else {
            viewModel.goBack()
        }
    }

    private fun handleNext() {
        when (viewModel.uiState.value.step) {
            CheckoutStep.ADDRESS -> viewModel.nextStep(
                name    = binding.checkoutInputName.text?.toString().orEmpty(),
                street  = binding.checkoutInputStreet.text?.toString().orEmpty(),
                city    = binding.checkoutInputCity.text?.toString().orEmpty(),
                zip     = binding.checkoutInputZip.text?.toString().orEmpty(),
                country = binding.checkoutSpinnerCountry.selectedItem?.toString().orEmpty()
            )
            CheckoutStep.PAYMENT -> viewModel.nextStepPayment(
                card   = binding.checkoutInputCard.text?.toString().orEmpty(),
                expiry = binding.checkoutInputExpiry.text?.toString().orEmpty(),
                cvv    = binding.checkoutInputCvv.text?.toString().orEmpty()
            )
            CheckoutStep.REVIEW -> viewModel.placeOrder()
        }
    }

    private fun renderStep(state: CheckoutUiState) {
        binding.checkoutStep1.isSelected = state.step == CheckoutStep.ADDRESS
        binding.checkoutStep2.isSelected = state.step == CheckoutStep.PAYMENT
        binding.checkoutStep3.isSelected = state.step == CheckoutStep.REVIEW

        binding.checkoutSectionAddress.visibility = if (state.step == CheckoutStep.ADDRESS) View.VISIBLE else View.GONE
        binding.checkoutSectionPayment.visibility = if (state.step == CheckoutStep.PAYMENT) View.VISIBLE else View.GONE
        binding.checkoutSectionReview.visibility  = if (state.step == CheckoutStep.REVIEW)  View.VISIBLE else View.GONE

        binding.checkoutButtonBack.visibility = if (state.step == CheckoutStep.ADDRESS) View.GONE else View.VISIBLE
        binding.checkoutButtonNext.text = when (state.step) {
            CheckoutStep.REVIEW -> getString(R.string.checkout_place_order)
            else                -> getString(R.string.checkout_next)
        }

        binding.checkoutLayoutName.error   = state.nameError
        binding.checkoutLayoutStreet.error = state.streetError
        binding.checkoutLayoutCity.error   = state.cityError
        binding.checkoutLayoutZip.error    = state.zipError

        binding.checkoutLayoutCard.error   = state.cardError
        binding.checkoutLayoutExpiry.error = state.expiryError
        binding.checkoutLayoutCvv.error    = state.cvvError

        if (state.step == CheckoutStep.REVIEW) {
            binding.checkoutTextAddressSummary.text =
                "${state.name}\n${state.street}\n${state.city} ${state.zip}\n${state.country}"
            binding.checkoutTextPaymentSummary.text = state.maskedCard
            binding.checkoutTextOrderTotal.text     = getString(R.string.checkout_total_format, state.orderTotal)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Formats expiry input as MM/YY automatically.
     *
     * The user types only digits on a numeric keypad (inputType=phone). After the 2nd digit the
     * watcher inserts '/'. Backspacing through the slash removes it automatically because we
     * re-derive the formatted string from raw digits each time, so "07/" → digits="07" → "07".
     */
    private inner class ExpiryTextWatcher : TextWatcher {
        private var isFormatting = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (isFormatting || s == null) return
            isFormatting = true

            val digits = s.filter { it.isDigit() }.take(4)
            val formatted: String = if (digits.length > 2) {
                "${digits.take(2)}/${digits.drop(2)}"
            } else {
                digits.toString()
            }

            if (s.toString() != formatted) {
                s.replace(0, s.length, formatted)
            }

            isFormatting = false
        }
    }
}
