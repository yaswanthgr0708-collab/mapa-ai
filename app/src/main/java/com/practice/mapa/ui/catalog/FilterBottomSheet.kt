package com.practice.mapa.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider
import com.practice.mapa.databinding.BottomSheetFilterBinding

class FilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activeCategories = arguments?.getStringArrayList(KEY_CATEGORIES)?.toSet()
            ?: setOf("Electronics", "Clothing", "Books", "Home")
        val minPrice = arguments?.getFloat(KEY_MIN_PRICE, 0f) ?: 0f
        val maxPrice = arguments?.getFloat(KEY_MAX_PRICE, 500f) ?: 500f

        binding.filterCheckElectronics.isChecked = "Electronics" in activeCategories
        binding.filterCheckClothing.isChecked    = "Clothing"    in activeCategories
        binding.filterCheckBooks.isChecked       = "Books"       in activeCategories
        binding.filterCheckHome.isChecked        = "Home"        in activeCategories

        binding.filterRangeSlider.valueFrom = 0f
        binding.filterRangeSlider.valueTo   = 500f
        binding.filterRangeSlider.stepSize  = 1f
        binding.filterRangeSlider.setValues(minPrice, maxPrice)
        updateSliderLabel(minPrice, maxPrice)

        binding.filterRangeSlider.addOnChangeListener(RangeSlider.OnChangeListener { slider, _, _ ->
            updateSliderLabel(slider.values[0], slider.values[1])
        })

        binding.filterButtonApply.setOnClickListener {
            val selected = buildSet {
                if (binding.filterCheckElectronics.isChecked) add("Electronics")
                if (binding.filterCheckClothing.isChecked)    add("Clothing")
                if (binding.filterCheckBooks.isChecked)       add("Books")
                if (binding.filterCheckHome.isChecked)        add("Home")
            }
            val values = binding.filterRangeSlider.values
            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                bundleOf(
                    KEY_CATEGORIES to ArrayList(selected),
                    KEY_MIN_PRICE  to values[0],
                    KEY_MAX_PRICE  to values[1]
                )
            )
            dismiss()
        }

        binding.filterButtonClear.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                bundleOf(
                    KEY_CATEGORIES to ArrayList(listOf("Electronics", "Clothing", "Books", "Home")),
                    KEY_MIN_PRICE  to 0f,
                    KEY_MAX_PRICE  to 500f
                )
            )
            dismiss()
        }
    }

    private fun updateSliderLabel(min: Float, max: Float) {
        binding.filterTextPriceRange.text = "$%.0f – $%.0f".format(min, max)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheet"
        const val RESULT_KEY  = "filter_result"
        const val KEY_CATEGORIES = "categories"
        const val KEY_MIN_PRICE  = "min_price"
        const val KEY_MAX_PRICE  = "max_price"

        fun newInstance(activeCategories: Set<String>, minPrice: Float, maxPrice: Float) =
            FilterBottomSheet().apply {
                arguments = bundleOf(
                    KEY_CATEGORIES to ArrayList(activeCategories),
                    KEY_MIN_PRICE  to minPrice,
                    KEY_MAX_PRICE  to maxPrice
                )
            }
    }
}
