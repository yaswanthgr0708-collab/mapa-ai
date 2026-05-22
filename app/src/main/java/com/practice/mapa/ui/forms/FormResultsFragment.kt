package com.practice.mapa.ui.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentFormResultsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormResultsFragment : Fragment() {

    private var _binding: FragmentFormResultsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments ?: Bundle()
        val on  = getString(R.string.forms_switch_on)
        val off = getString(R.string.forms_switch_off)

        binding.resultsTextText.text       = args.getString("resultText", "")
        binding.resultsTextNumber.text     = args.getString("resultNumber", "")
        binding.resultsTextEmail.text      = args.getString("resultEmail", "")
        binding.resultsTextPassword.text   = args.getString("resultPassword", "")
        binding.resultsTextMultiline.text  = args.getString("resultMultiline", "")
        binding.resultsTextDate.text       = args.getString("resultDate", "")
        binding.resultsTextTime.text       = args.getString("resultTime", "")
        binding.resultsTextCountry.text    = args.getString("resultCountry", "")
        binding.resultsTextRadio.text      = args.getString("resultRadio", "")
        binding.resultsTextCheckboxes.text = args.getString("resultCheckboxes", "")
        binding.resultsTextSwitch1.text    = if (args.getBoolean("resultSwitch1")) on else off
        binding.resultsTextSwitch2.text    = if (args.getBoolean("resultSwitch2")) on else off
        binding.resultsTextSwitch3.text    = if (args.getBoolean("resultSwitch3")) on else off
        binding.resultsTextSeekbar.text    = args.getInt("resultSeekbar", 0).toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
