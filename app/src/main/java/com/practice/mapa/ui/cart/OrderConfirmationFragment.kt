package com.practice.mapa.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentOrderConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderConfirmationFragment : Fragment() {

    private var _binding: FragmentOrderConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderId    = arguments?.getString("orderId")           ?: "ORD-000000"
        val orderTotal = (arguments?.getFloat("orderTotal") ?: 0f).toDouble()

        binding.confirmTextOrderId.text  = orderId
        binding.confirmTextOrderId.contentDescription = orderId
        binding.confirmTextTotal.text    = getString(R.string.checkout_total_format, orderTotal)
        binding.confirmTextMessage.text  = getString(R.string.confirm_message)

        binding.confirmButtonHome.setOnClickListener {
            findNavController().navigate(
                R.id.homeFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
