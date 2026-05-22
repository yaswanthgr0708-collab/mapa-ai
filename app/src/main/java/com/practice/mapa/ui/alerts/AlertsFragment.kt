package com.practice.mapa.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentAlertsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.alertsTextResult.text = getString(R.string.alerts_result_none)

        // US-G1: AlertDialog
        binding.alertsButtonDialog.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.alerts_dialog_title)
                .setMessage(R.string.alerts_dialog_message)
                .setPositiveButton(R.string.alerts_dialog_ok) { _, _ ->
                    binding.alertsTextResult.text = getString(R.string.alerts_dialog_result_ok)
                }
                .setNegativeButton(R.string.alerts_dialog_cancel) { _, _ ->
                    binding.alertsTextResult.text = getString(R.string.alerts_dialog_result_cancel)
                }
                .show()
        }

        // US-G2: Bottom sheet
        binding.alertsButtonBottomSheet.setOnClickListener {
            val sheet = AlertsBottomSheet()
            sheet.onAction = { action ->
                binding.alertsTextResult.text =
                    getString(R.string.alerts_sheet_result_format, action)
            }
            sheet.show(childFragmentManager, "alerts_bottom_sheet")
        }

        // US-G3: Snackbar with Undo
        binding.alertsButtonSnackbar.setOnClickListener {
            binding.alertsTextResult.text = getString(R.string.alerts_snackbar_result_shown)
            Snackbar.make(binding.root, R.string.alerts_snackbar_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.alerts_snackbar_undo) {
                    binding.alertsTextResult.text = getString(R.string.alerts_snackbar_result_undone)
                }
                .show()
        }

        // US-G4: Toast
        binding.alertsButtonToast.setOnClickListener {
            Toast.makeText(requireContext(), R.string.alerts_toast_message, Toast.LENGTH_SHORT).show()
        }

        // US-G5: Fullscreen dialog
        binding.alertsButtonFullscreen.setOnClickListener {
            FullscreenDialogFragment().show(childFragmentManager, "alerts_fullscreen")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
