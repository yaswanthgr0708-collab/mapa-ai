package com.practice.mapa.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.practice.mapa.data.catalog.Product
import com.practice.mapa.databinding.ItemHomeFeaturedBinding
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
            featuredItemIcon.setImageResource(ProductImageUtil.imageResFor(product.category))
            featuredItemIcon.setBackgroundColor(ProductImageUtil.backgroundColorFor(product.category))
            featuredItemName.text = product.name
            featuredItemPrice.text = "$%.2f".format(product.price)
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
