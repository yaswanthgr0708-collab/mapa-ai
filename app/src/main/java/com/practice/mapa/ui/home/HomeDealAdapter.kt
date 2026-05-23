package com.practice.mapa.ui.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.practice.mapa.R
import com.practice.mapa.data.catalog.Product
import com.practice.mapa.databinding.ItemHomeDealBinding
import com.practice.mapa.util.PriceUtil
import com.practice.mapa.util.ProductImageUtil

class HomeDealAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, HomeDealAdapter.DealViewHolder>(DIFF) {

    inner class DealViewHolder(val binding: ItemHomeDealBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val binding = ItemHomeDealBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val product = getItem(position)
        with(holder.binding) {
            dealItemImage.setBackgroundColor(ProductImageUtil.backgroundColorFor(product.category))
            dealItemImage.load(
                ProductImageUtil.getProductImageRes(root.context, product.category, product.imageIndex)
            ) {
                crossfade(true)
                placeholder(ProductImageUtil.fallbackRes(product.category))
                error(ProductImageUtil.fallbackRes(product.category))
            }
            dealItemName.text = product.name

            dealItemBadge.text = root.context.getString(
                R.string.discount_badge_format, product.discountPercentage
            )

            dealItemPriceOriginal.text = PriceUtil.formatCents(product.priceCents)
            dealItemPriceOriginal.paintFlags =
                dealItemPriceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            dealItemPriceCurrent.text = PriceUtil.formatCents(product.discountedPriceCents)

            root.contentDescription = "deals_item_$position"
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
