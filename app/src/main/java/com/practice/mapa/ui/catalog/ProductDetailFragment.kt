package com.practice.mapa.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.practice.mapa.R
import com.practice.mapa.data.catalog.Product
import com.practice.mapa.databinding.FragmentProductDetailBinding
import com.practice.mapa.util.ProductImageUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductDetailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getInt("productId") ?: 0
        viewModel.loadProduct(productId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.product.collect { product ->
                if (product != null) bindProduct(product)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addedToCart.collect { productId ->
                Snackbar.make(binding.root, R.string.catalog_added_to_cart, Snackbar.LENGTH_LONG)
                    .setAction(R.string.cart_undo) { viewModel.undoAddToCart(productId) }
                    .show()
            }
        }
    }

    private fun bindProduct(product: Product) {
        binding.detailTextName.text = product.name
        binding.detailTextCategory.text = product.category
        binding.detailTextPrice.text = "$%.2f".format(product.price)
        binding.detailTextDescription.text = product.description
        binding.detailImage.setImageResource(ProductImageUtil.imageResFor(product.category))
        binding.detailImage.setBackgroundColor(ProductImageUtil.backgroundColorFor(product.category))
        binding.detailButtonAddToCart.setOnClickListener {
            viewModel.addToCart(product.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
