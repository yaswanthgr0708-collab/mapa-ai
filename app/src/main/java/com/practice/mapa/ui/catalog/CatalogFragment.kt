package com.practice.mapa.ui.catalog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentCatalogBinding
import com.practice.mapa.ui.common.MapaLoadingView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatalogViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    // Saved before view is destroyed (e.g. navigating to ProductDetail); restored on return.
    private var savedScrollState: Parcelable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductAdapter { product ->
            findNavController().navigate(
                R.id.action_catalogFragment_to_productDetailFragment,
                bundleOf("productId" to product.id)
            )
        }

        binding.catalogRecycler.adapter = adapter
        binding.catalogRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager ?: return
                val lastVisible = when (lm) {
                    is GridLayoutManager   -> lm.findLastVisibleItemPosition()
                    is LinearLayoutManager -> lm.findLastVisibleItemPosition()
                    else -> return
                }
                if (lastVisible >= adapter.itemCount - 5) viewModel.loadMore()
            }
        })

        binding.catalogSearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQuery(newText.orEmpty())
                return true
            }
        })

        binding.catalogButtonToggleLayout.setOnClickListener { viewModel.toggleLayout() }

        binding.catalogFabFilter.setOnClickListener {
            val state = viewModel.uiState.value
            FilterBottomSheet.newInstance(state.activeCategories, state.minPrice, state.maxPrice)
                .show(childFragmentManager, FilterBottomSheet.TAG)
        }

        childFragmentManager.setFragmentResultListener(FilterBottomSheet.RESULT_KEY, viewLifecycleOwner) { _, bundle ->
            val categories = bundle.getStringArrayList(FilterBottomSheet.KEY_CATEGORIES)?.toSet() ?: emptySet()
            val minPrice = bundle.getFloat(FilterBottomSheet.KEY_MIN_PRICE, 0f)
            val maxPrice = bundle.getFloat(FilterBottomSheet.KEY_MAX_PRICE, 500f)
            viewModel.applyFilters(categories, minPrice, maxPrice)
        }

        binding.catalogLoadingInline.setMode(MapaLoadingView.Mode.INLINE)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                applyLayoutMode(state.isGridLayout)
                adapter.isGridLayout = state.isGridLayout

                // Restore scroll once — after the diffed list is dispatched to the RecyclerView.
                val scrollToRestore = savedScrollState
                adapter.submitList(state.products.toList()) {
                    if (scrollToRestore != null) {
                        binding.catalogRecycler.layoutManager?.onRestoreInstanceState(scrollToRestore)
                        savedScrollState = null
                    }
                }

                renderEmptyState(state)
                renderFilterChips(state)
                // Show inline spinner only during pagination (products already loaded).
                // Initial load is fast (in-memory); spinner during that would flash.
                if (state.isLoading && state.products.isNotEmpty()) {
                    binding.catalogLoadingInline.show(getString(R.string.loading_more_products))
                } else {
                    binding.catalogLoadingInline.hide()
                }
            }
        }
    }

    private fun applyLayoutMode(isGrid: Boolean) {
        val lm = if (isGrid) GridLayoutManager(requireContext(), 2) else LinearLayoutManager(requireContext())
        if (binding.catalogRecycler.layoutManager?.javaClass != lm.javaClass) {
            binding.catalogRecycler.layoutManager = lm
        }
        binding.catalogButtonToggleLayout.contentDescription =
            if (isGrid) "catalog_button_toggle_list" else "catalog_button_toggle_grid"
        binding.catalogButtonToggleLayout.setImageResource(
            if (isGrid) R.drawable.ic_list else R.drawable.ic_grid
        )
    }

    private fun renderEmptyState(state: CatalogUiState) {
        val showEmpty = state.products.isEmpty() && state.searchQuery.isNotEmpty() && !state.isLoading
        binding.catalogTextEmpty.visibility = if (showEmpty) View.VISIBLE else View.GONE
        binding.catalogRecycler.visibility  = if (showEmpty) View.GONE   else View.VISIBLE
        if (showEmpty) {
            binding.catalogTextEmpty.text = getString(R.string.catalog_no_results_format, state.searchQuery)
        }
    }

    private fun renderFilterChips(state: CatalogUiState) {
        binding.catalogChipGroup.removeAllViews()
        val allCategories = setOf("Electronics", "Clothing", "Books", "Home")
        if (state.activeCategories != allCategories) {
            state.activeCategories.forEach { category ->
                val chip = Chip(requireContext()).apply {
                    text = category
                    isCloseIconVisible = true
                    contentDescription = "catalog_chip_filter_${category.lowercase()}"
                    setOnCloseIconClickListener { viewModel.removeCategoryFilter(category) }
                }
                binding.catalogChipGroup.addView(chip)
            }
        }
        val isPriceFiltered = state.minPrice > 0f || state.maxPrice < 500f
        if (isPriceFiltered) {
            val chip = Chip(requireContext()).apply {
                text = "$%.0f–$%.0f".format(state.minPrice, state.maxPrice)
                isCloseIconVisible = true
                contentDescription = "catalog_chip_filter_price"
                setOnCloseIconClickListener { viewModel.clearPriceFilter() }
            }
            binding.catalogChipGroup.addView(chip)
        }
        binding.catalogChipGroup.visibility =
            if (binding.catalogChipGroup.childCount > 0) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        savedScrollState = binding.catalogRecycler.layoutManager?.onSaveInstanceState()
        super.onDestroyView()
        _binding = null
    }
}
