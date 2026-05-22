package com.practice.mapa.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.practice.mapa.databinding.BottomSheetAlertsBinding

class AlertsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAlertsBinding? = null
    private val binding get() = _binding!!

    var onAction: ((String) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.alertsSheetButton1.setOnClickListener { dismiss(); onAction?.invoke("Action 1") }
        binding.alertsSheetButton2.setOnClickListener { dismiss(); onAction?.invoke("Action 2") }
        binding.alertsSheetButton3.setOnClickListener { dismiss(); onAction?.invoke("Action 3") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
