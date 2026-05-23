package com.practice.mapa.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var bannerAdapter: HomeBannerAdapter
    private lateinit var featuredAdapter: HomeFeaturedAdapter
    private lateinit var dealAdapter: HomeDealAdapter

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val b = _binding ?: return
            val next = (b.homeCarouselPager.currentItem + 1) % BANNER_COUNT
            b.homeCarouselPager.setCurrentItem(next, true)
            autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY_MS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeTextGreeting.text = viewModel.greeting

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.username.collect { username ->
                val welcomeText = if (username.isNotEmpty())
                    getString(R.string.home_welcome_format, username) else ""
                binding.homeTextWelcome.text = welcomeText
                binding.homeTextWelcome.contentDescription = welcomeText
            }
        }

        setupCarousel()
        setupCategoryGrid()
        setupFeaturedProducts()
        setupDeals()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.featuredProducts.collect { products ->
                featuredAdapter.submitList(products)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flashDeals.collect { products ->
                dealAdapter.submitList(products)
            }
        }
    }

    private fun setupCarousel() {
        val ctx = requireContext()
        val banners = listOf(
            BannerItem(
                getString(R.string.banner_electronics_headline),
                getString(R.string.banner_electronics_subline),
                getString(R.string.banner_electronics_cta),
                ContextCompat.getColor(ctx, R.color.category_electronics),
                "Electronics"
            ),
            BannerItem(
                getString(R.string.banner_clothing_headline),
                getString(R.string.banner_clothing_subline),
                getString(R.string.banner_clothing_cta),
                ContextCompat.getColor(ctx, R.color.category_clothing),
                "Clothing"
            ),
            BannerItem(
                getString(R.string.banner_books_headline),
                getString(R.string.banner_books_subline),
                getString(R.string.banner_books_cta),
                ContextCompat.getColor(ctx, R.color.category_books),
                "Books"
            ),
            BannerItem(
                getString(R.string.banner_home_headline),
                getString(R.string.banner_home_subline),
                getString(R.string.banner_home_cta),
                ContextCompat.getColor(ctx, R.color.category_home),
                "Home"
            )
        )
        bannerAdapter = HomeBannerAdapter(banners) { category -> navigateToCatalog(category) }
        binding.homeCarouselPager.adapter = bannerAdapter
        TabLayoutMediator(binding.homeCarouselIndicator, binding.homeCarouselPager) { tab, _ ->
            tab.text = null
        }.attach()
    }

    private fun setupCategoryGrid() {
        binding.homeCategoryElectronics.setOnClickListener { navigateToCatalog("Electronics") }
        binding.homeCategoryClothing.setOnClickListener { navigateToCatalog("Clothing") }
        binding.homeCategoryBooks.setOnClickListener { navigateToCatalog("Books") }
        binding.homeCategoryHome.setOnClickListener { navigateToCatalog("Home") }
    }

    private fun setupFeaturedProducts() {
        featuredAdapter = HomeFeaturedAdapter { product ->
            findNavController().navigate(
                R.id.action_homeFragment_to_productDetailFragment,
                bundleOf("productId" to product.id)
            )
        }
        binding.homeFeaturedRecycler.apply {
            adapter = featuredAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupDeals() {
        dealAdapter = HomeDealAdapter { product ->
            findNavController().navigate(
                R.id.action_homeFragment_to_productDetailFragment,
                bundleOf("productId" to product.id)
            )
        }
        binding.homeDealsRecycler.apply {
            adapter = dealAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun navigateToCatalog(category: String) {
        findNavController().navigate(
            R.id.action_homeFragment_to_catalogFragment,
            bundleOf("categoryFilter" to category)
        )
    }

    override fun onResume() {
        super.onResume()
        if (!com.practice.mapa.util.TestMode.isEnabled) {
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY_MS)
        }
    }

    override fun onPause() {
        super.onPause()
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }

    override fun onDestroyView() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val BANNER_COUNT = 4
        const val AUTO_SCROLL_DELAY_MS = 4000L
    }
}
