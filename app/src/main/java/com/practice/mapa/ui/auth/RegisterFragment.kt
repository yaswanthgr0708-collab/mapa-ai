package com.practice.mapa.ui.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate country spinner from string array resource
        val countries = resources.getStringArray(R.array.countries)
        binding.registerSpinnerCountry.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            countries
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Date of birth picker (defaults to 18 years ago)
        binding.registerPickerDob.setOnClickListener {
            val cal = Calendar.getInstance().apply { add(Calendar.YEAR, -18) }
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val dateStr = "%04d-%02d-%02d".format(year, month + 1, day)
                    binding.registerTextDob.text = dateStr
                    // Stable contentDescription so Appium can assert the selected value
                    binding.registerTextDob.contentDescription = "register_text_dob_$dateStr"
                    binding.registerTextDobError.visibility = View.GONE
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.registerButtonSubmit.setOnClickListener {
            clearErrors()
            viewModel.register(
                name            = binding.registerInputName.text?.toString() ?: "",
                email           = binding.registerInputEmail.text?.toString() ?: "",
                password        = binding.registerInputPassword.text?.toString() ?: "",
                confirmPassword = binding.registerInputConfirmPassword.text?.toString() ?: "",
                dob             = binding.registerTextDob.text?.toString() ?: "",
                termsAccepted   = binding.registerCheckboxTerms.isChecked
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state -> render(state) }
        }
    }

    private fun clearErrors() {
        binding.registerLayoutName.error            = null
        binding.registerLayoutEmail.error           = null
        binding.registerLayoutPassword.error        = null
        binding.registerLayoutConfirmPassword.error = null
        binding.registerTextDobError.visibility     = View.GONE
    }

    private fun render(state: RegisterUiState) {
        when (state) {
            RegisterUiState.Idle -> Unit

            RegisterUiState.Success -> {
                // Signal LoginFragment to show the success snackbar (US-A2 AC4), then go back
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("register_success", true)
                viewModel.resetState()
                findNavController().popBackStack()
            }

            is RegisterUiState.FieldErrors -> {
                val required = getString(R.string.error_field_required)
                if (state.nameError)     binding.registerLayoutName.error  = required
                if (state.emailError)    binding.registerLayoutEmail.error = required
                if (state.passwordError) binding.registerLayoutPassword.error = required
                if (state.dobError) {
                    binding.registerTextDobError.text       = required
                    binding.registerTextDobError.visibility = View.VISIBLE
                }
            }

            RegisterUiState.PasswordMismatch ->
                binding.registerLayoutConfirmPassword.error =
                    getString(R.string.register_error_password_mismatch)

            RegisterUiState.TermsError -> {
                Snackbar.make(binding.root, R.string.register_error_terms, Snackbar.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
