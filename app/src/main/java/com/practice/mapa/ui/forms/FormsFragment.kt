package com.practice.mapa.ui.forms

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentFormsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class FormsFragment : Fragment() {

    private var _binding: FragmentFormsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FormsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCountrySpinner()
        setupSeekBar()
        setupSwitches()
        setupCheckboxes()
        setupDatePicker()
        setupTimePicker()
        setupSubmit()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                renderErrors(state)
                renderDynamicText(state)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.event.collect { event ->
                if (event is FormsEvent.Submit) {
                    viewModel.consumeEvent()
                    findNavController().navigate(
                        R.id.action_formsFragment_to_formResultsFragment,
                        bundleOf(
                            "resultText"       to event.textValue,
                            "resultNumber"     to event.numberValue,
                            "resultEmail"      to event.emailValue,
                            "resultPassword"   to event.passwordValue,
                            "resultMultiline"  to event.multilineValue,
                            "resultDate"       to event.dateValue,
                            "resultTime"       to event.timeValue,
                            "resultCountry"    to event.countryValue,
                            "resultRadio"      to event.radioValue,
                            "resultCheckboxes" to event.checkboxSummary,
                            "resultSwitch1"    to event.switch1,
                            "resultSwitch2"    to event.switch2,
                            "resultSwitch3"    to event.switch3,
                            "resultSeekbar"    to event.seekbarValue
                        )
                    )
                }
            }
        }
    }

    private fun setupCountrySpinner() {
        val countries = resources.getStringArray(R.array.countries)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.formsSpinnerCountry.adapter = adapter
    }

    private fun setupSeekBar() {
        binding.formsTextSeekbarValue.text = getString(R.string.forms_seekbar_value_format, 0)
        binding.formsSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.setSeekbar(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupSwitches() {
        val off = getString(R.string.forms_switch_off)
        binding.formsSwitch1State.text = off
        binding.formsSwitch2State.text = off
        binding.formsSwitch3State.text = off

        binding.formsSwitch1.setOnCheckedChangeListener { _, checked ->
            viewModel.setSwitch1(checked)
            binding.formsSwitch1State.text = if (checked) getString(R.string.forms_switch_on) else getString(R.string.forms_switch_off)
        }
        binding.formsSwitch2.setOnCheckedChangeListener { _, checked ->
            viewModel.setSwitch2(checked)
            binding.formsSwitch2State.text = if (checked) getString(R.string.forms_switch_on) else getString(R.string.forms_switch_off)
        }
        binding.formsSwitch3.setOnCheckedChangeListener { _, checked ->
            viewModel.setSwitch3(checked)
            binding.formsSwitch3State.text = if (checked) getString(R.string.forms_switch_on) else getString(R.string.forms_switch_off)
        }
    }

    private fun setupCheckboxes() {
        val listener = { _: android.widget.CompoundButton, _: Boolean ->
            viewModel.updateCheckboxSummary(
                binding.formsCheckboxA.isChecked,
                binding.formsCheckboxB.isChecked,
                binding.formsCheckboxC.isChecked
            )
        }
        binding.formsCheckboxA.setOnCheckedChangeListener(listener)
        binding.formsCheckboxB.setOnCheckedChangeListener(listener)
        binding.formsCheckboxC.setOnCheckedChangeListener(listener)
        viewModel.updateCheckboxSummary(false, false, false)
    }

    private fun setupDatePicker() {
        binding.formsButtonPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    viewModel.setDate("%04d-%02d-%02d".format(year, month + 1, day))
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTimePicker() {
        binding.formsButtonPickTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    viewModel.setTime("%02d:%02d".format(hour, minute))
                },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
            ).show()
        }
    }

    private fun setupSubmit() {
        binding.formsButtonSubmit.setOnClickListener {
            val radioId = binding.formsRadioGroup.checkedRadioButtonId
            val radioValue = when (radioId) {
                R.id.forms_radio_option_a -> getString(R.string.forms_radio_option_a)
                R.id.forms_radio_option_b -> getString(R.string.forms_radio_option_b)
                R.id.forms_radio_option_c -> getString(R.string.forms_radio_option_c)
                else -> ""
            }
            viewModel.submit(
                text      = binding.formsInputText.text?.toString().orEmpty(),
                number    = binding.formsInputNumber.text?.toString().orEmpty(),
                email     = binding.formsInputEmail.text?.toString().orEmpty(),
                password  = binding.formsInputPassword.text?.toString().orEmpty(),
                multiline = binding.formsInputMultiline.text?.toString().orEmpty(),
                country   = binding.formsSpinnerCountry.selectedItem?.toString().orEmpty(),
                radio     = radioValue,
                checkboxA = binding.formsCheckboxA.isChecked,
                checkboxB = binding.formsCheckboxB.isChecked,
                checkboxC = binding.formsCheckboxC.isChecked
            )
        }
    }

    private fun renderErrors(state: FormsUiState) {
        binding.formsLayoutText.error     = state.textError
        binding.formsLayoutNumber.error   = state.numberError
        binding.formsLayoutEmail.error    = state.emailError
        binding.formsLayoutPassword.error = state.passwordError

        binding.formsErrorDate.text       = state.dateError ?: ""
        binding.formsErrorDate.visibility = if (state.dateError != null) View.VISIBLE else View.GONE
        binding.formsErrorTime.text       = state.timeError ?: ""
        binding.formsErrorTime.visibility = if (state.timeError != null) View.VISIBLE else View.GONE
    }

    private fun renderDynamicText(state: FormsUiState) {
        binding.formsTextDate.text           = state.dateValue
        binding.formsTextTime.text           = state.timeValue
        binding.formsTextCheckboxSummary.text = state.checkboxSummary
        binding.formsTextSeekbarValue.text   = getString(R.string.forms_seekbar_value_format, state.seekbarValue)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
