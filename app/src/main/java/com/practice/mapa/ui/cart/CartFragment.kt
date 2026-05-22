package com.practice.mapa.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.practice.mapa.R
import com.practice.mapa.data.catalog.CartItemWithProduct
import com.practice.mapa.databinding.FragmentCartBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CartAdapter(
            onIncrease = { viewModel.increaseQuantity(it) },
            onDecrease = { viewModel.decreaseQuantity(it) }
        )
        binding.cartRecycler.adapter = adapter
        binding.cartRecycler.layoutManager = LinearLayoutManager(requireContext())

        val swipeCallback = SwipeToDeleteCallback { position ->
            val item = adapter.getItemAt(position)
            viewModel.removeItem(item.cartItem.productId)
            showUndoSnackbar(item)
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.cartRecycler)

        binding.cartButtonCheckout.setOnClickListener {
            val total = viewModel.cartItems.value.sumOf { it.product.price * it.cartItem.quantity }
            findNavController().navigate(
                R.id.action_cartFragment_to_checkoutFragment,
                android.os.Bundle().apply { putFloat("orderTotal", total.toFloat()) }
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItems.collect { items ->
                adapter.submitList(items)
                val empty = items.isEmpty()
                binding.cartTextEmpty.visibility      = if (empty) View.VISIBLE else View.GONE
                binding.cartRecycler.visibility       = if (empty) View.GONE    else View.VISIBLE
                binding.cartButtonCheckout.visibility = if (empty) View.GONE    else View.VISIBLE
            }
        }
    }

    private fun showUndoSnackbar(item: CartItemWithProduct) {
        Snackbar.make(binding.root, getString(R.string.cart_item_removed, item.product.name), Snackbar.LENGTH_LONG)
            .setAction(R.string.cart_undo) { viewModel.restoreItem(item) }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
