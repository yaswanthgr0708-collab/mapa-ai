package com.practice.mapa.ui.system

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.practice.mapa.MainActivity
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentSystemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SystemFragment : Fragment() {

    private var _binding: FragmentSystemBinding? = null
    private val binding get() = _binding!!

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        updatePermissionStatus(granted)
    }

    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showNotification()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSystemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reflect current camera permission state on screen entry
        val alreadyGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        updatePermissionStatus(alreadyGranted)

        // US-J1: Camera permission
        binding.systemButtonCameraPermission.setOnClickListener {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // US-J2: Share intent
        binding.systemButtonShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.system_share_text))
            }
            startActivity(Intent.createChooser(intent, null))
        }

        // US-J3: Notification
        binding.systemButtonNotification.setOnClickListener {
            ensureNotificationChannel()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (hasPermission) showNotification()
                else notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showNotification()
            }
        }
    }

    private fun updatePermissionStatus(granted: Boolean) {
        binding.systemTextPermissionStatus.text = if (granted)
            getString(R.string.system_text_permission_status_granted)
        else
            getString(R.string.system_text_permission_status_denied)
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.system_notification_channel_id)
            val channel = NotificationChannel(
                channelId,
                getString(R.string.system_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nm = requireContext().getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val channelId = getString(R.string.system_notification_channel_id)
        val tapIntent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            requireContext(), 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.system_notification_title))
            .setContentText(getString(R.string.system_notification_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(requireContext()).notify(1001, notification)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
