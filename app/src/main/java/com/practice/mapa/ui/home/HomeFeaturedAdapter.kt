package com.practice.mapa.ui.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.practice.mapa.R
import com.practice.mapa.data.catalog.Product
import com.practice.mapa.databinding.ItemHomeFeaturedBinding
import com.practice.mapa.util.PriceUtil
import com.practice.mapa.util.ProductImageUtil

class HomeFeaturedAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, HomeFeaturedAdapter.FeaturedViewHolder>(DIFF) {

    inner class FeaturedViewHolder(val binding: ItemHomeFeaturedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedViewHolder {
        val binding = ItemHomeFeaturedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FeaturedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeaturedViewHolder, position: Int) {
        val product = getItem(position)
        with(holder.binding) {
            featuredItemIcon.setBackgroundColor(ProductImageUtil.backgroundColorFor(product.category))
            featuredItemIcon.load(
                ProductImageUtil.getProductImageRes(root.context, product.category, product.imageIndex)
            ) {
                crossfade(true)
                placeholder(ProductImageUtil.fallbackRes(product.category))
                error(ProductImageUtil.fallbackRes(product.category))
            }
            featuredItemName.text = product.name

            val discounted = product.discountPercentage > 0
            featuredItemPriceCurrent.text = PriceUtil.formatCents(product.discountedPriceCents)

            if (discounted) {
                featuredItemBadge.text = root.context.getString(
                    R.string.discount_badge_format, product.discountPercentage
                )
                featuredItemBadge.visibility = View.VISIBLE

                featuredItemPriceOriginal.text = PriceUtil.formatCents(product.priceCents)
                featuredItemPriceOriginal.paintFlags =
                    featuredItemPriceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                featuredItemPriceOriginal.visibility = View.VISIBLE
            } else {
                featuredItemBadge.visibility = View.GONE
                featuredItemPriceOriginal.visibility = View.GONE
            }

            root.contentDescription = "featured_item_$position"
            root.setOnClickListener { onItemClick(product) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(old: Product, new: Product) = old.id == new.id
            override fun areContentsTheSame(old: Product, new: Product) = old == new
        }
    }
}
