package com.practice.mapa.ui.auth

import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practice.mapa.R
import com.practice.mapa.databinding.DialogForgotPasswordBinding

/**
 * Forgot Password dialog (US-A3).
 *
 * Declared as a <dialog> destination in nav_graph.xml so it is accessible via
 * navController.navigate(R.id.action_loginFragment_to_forgotPasswordDialog).
 *
 * The Send and Cancel buttons are provided by the AlertDialog chrome; their
 * contentDescriptions are set programmatically so Appium can locate them via
 * accessibility id (forgot_button_send / forgot_button_cancel).
 */
class ForgotPasswordDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogForgotPasswordBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.forgot_title)
            .setView(binding.root)
            .setPositiveButton(R.string.forgot_button_send, null)   // null → we override to prevent auto-dismiss
            .setNegativeButton(R.string.forgot_button_cancel) { _, _ -> dismiss() }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                contentDescription = "forgot_button_send"
                setOnClickListener { handleSend(binding, dialog) }
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ?.contentDescription = "forgot_button_cancel"
        }

        return dialog
    }

    private fun handleSend(binding: DialogForgotPasswordBinding, dialog: AlertDialog) {
        val email = binding.forgotInputEmail.text?.toString()?.trim() ?: ""
        if (email.isBlank()) {
            binding.forgotLayoutEmail.error = getString(R.string.forgot_error_email_required)
            return
        }
        binding.forgotLayoutEmail.error = null
        Toast.makeText(
            requireContext(),
            getString(R.string.forgot_success_format, email),
            Toast.LENGTH_LONG
        ).show()
        dialog.dismiss()
    }
}
