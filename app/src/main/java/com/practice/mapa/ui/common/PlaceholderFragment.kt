package com.practice.mapa.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practice.mapa.databinding.FragmentPlaceholderBinding

/**
 * Phase-1 stub. Every tab fragment extends this and supplies its display name
 * via [screenName]. As features are built out (Phase 2+), each subclass replaces
 * onViewCreated with its real implementation.
 */
abstract class PlaceholderFragment : Fragment() {

    private var _binding: FragmentPlaceholderBinding? = null
    private val binding get() = _binding!!

    /** The label shown in the placeholder — also used as part of the screen-level resource-id. */
    abstract val screenName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceholderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholderText.text = screenName
        // contentDescription is what Appium reads as `accessibility id`
        binding.placeholderText.contentDescription = "placeholder_${screenName.lowercase()}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
