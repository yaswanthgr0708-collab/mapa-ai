package com.practice.mapa.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    private var ignoringSpinnerEvents = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLanguageSpinner()
        setupClearData()

        // Theme switch — driven by ViewModel state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nightMode.collect { mode ->
                binding.settingsSwitchDarkMode.isChecked = mode == AppCompatDelegate.MODE_NIGHT_YES
            }
        }
        binding.settingsSwitchDarkMode.setOnCheckedChangeListener { _, checked ->
            viewModel.setNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Notification switches — driven by ViewModel state, guard against re-entrancy
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notifPromo.collect { binding.settingsSwitchNotifPromo.isChecked = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notifOrders.collect { binding.settingsSwitchNotifOrders.isChecked = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notifProducts.collect { binding.settingsSwitchNotifProducts.isChecked = it }
        }
        binding.settingsSwitchNotifPromo.setOnCheckedChangeListener { _, checked ->
            viewModel.setNotifPromo(checked)
        }
        binding.settingsSwitchNotifOrders.setOnCheckedChangeListener { _, checked ->
            viewModel.setNotifOrders(checked)
        }
        binding.settingsSwitchNotifProducts.setOnCheckedChangeListener { _, checked ->
            viewModel.setNotifProducts(checked)
        }

        // Navigate to login after clear data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToLogin.collect { go ->
                if (go) {
                    viewModel.onNavigatedToLogin()
                    findNavController().navigate(
                        R.id.loginFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(findNavController().graph.id, true)
                            .build()
                    )
                }
            }
        }
    }

    private fun setupLanguageSpinner() {
        val languages = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.settingsSpinnerLanguage.adapter = adapter

        // Reflect current locale
        val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        ignoringSpinnerEvents = true
        binding.settingsSpinnerLanguage.setSelection(
            when {
                currentTag.startsWith("fr") -> 1
                currentTag.startsWith("es") -> 2
                else -> 0
            }
        )
        ignoringSpinnerEvents = false

        binding.settingsSpinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (ignoringSpinnerEvents) return
                val localeList = when (position) {
                    1    -> LocaleListCompat.forLanguageTags("fr")
                    2    -> LocaleListCompat.forLanguageTags("es")
                    else -> LocaleListCompat.getEmptyLocaleList()
                }
                AppCompatDelegate.setApplicationLocales(localeList)
                // Activity is recreated automatically by AppCompat after locale change
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupClearData() {
        binding.settingsButtonClearData.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.settings_clear_data_title)
                .setMessage(R.string.settings_clear_data_message)
                .setPositiveButton(R.string.settings_clear_data_confirm) { _, _ -> viewModel.clearData() }
                .setNegativeButton(R.string.settings_clear_data_cancel, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
