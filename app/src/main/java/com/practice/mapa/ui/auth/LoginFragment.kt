package com.practice.mapa.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentLoginBinding
import com.practice.mapa.ui.common.MapaLoadingView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show snackbar if we just came back from a successful registration (US-A2 AC4).
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("register_success")
            ?.observe(viewLifecycleOwner) { success ->
                if (success == true) {
                    Snackbar.make(binding.root, R.string.register_success, Snackbar.LENGTH_LONG).show()
                }
            }

        binding.loginButtonSubmit.setOnClickListener {
            clearErrors()
            val username = binding.loginInputUsername.text?.toString() ?: ""
            val password = binding.loginInputPassword.text?.toString() ?: ""
            viewModel.login(username, password)
        }

        binding.loginButtonForgot.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordDialog)
        }

        binding.loginButtonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginLoadingOverlay.setMode(MapaLoadingView.Mode.OVERLAY)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state -> render(state) }
        }
    }

    private fun clearErrors() {
        binding.loginLayoutUsername.error = null
        binding.loginLayoutPassword.error = null
        binding.loginTextError.visibility = View.GONE
    }

    private fun render(state: LoginUiState) {
        val loading = state is LoginUiState.Loading
        binding.loginButtonSubmit.isEnabled = !loading
        if (loading) {
            binding.loginLoadingOverlay.show(getString(R.string.loading_signing_in))
        } else {
            binding.loginLoadingOverlay.hide()
        }

        when (state) {
            LoginUiState.Idle, LoginUiState.Loading -> Unit

            is LoginUiState.NavigateToHome -> {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                viewModel.resetState()
            }

            is LoginUiState.FieldErrors -> {
                if (state.showUsernameError)
                    binding.loginLayoutUsername.error = getString(R.string.login_error_username_required)
                if (state.showPasswordError)
                    binding.loginLayoutPassword.error = getString(R.string.login_error_password_required)
            }

            // Inline error shown below the form (stays until next submit)
            LoginUiState.InvalidCredentials ->
                binding.loginLayoutPassword.error = getString(R.string.login_error_invalid_credentials)

            LoginUiState.AccountLocked -> {
                binding.loginTextError.text = getString(R.string.login_error_account_locked)
                binding.loginTextError.visibility = View.VISIBLE
            }

            // error_user: transient toast (HANDOFF §5.1)
            LoginUiState.ServerError -> {
                Toast.makeText(requireContext(), R.string.login_error_server, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
