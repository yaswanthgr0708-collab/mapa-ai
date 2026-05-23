package com.practice.mapa.ui.catalog

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.practice.mapa.R
import com.practice.mapa.data.catalog.Product
import com.practice.mapa.util.PriceUtil
import com.practice.mapa.util.ProductImageUtil

class ProductAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF) {

    var isGridLayout = true

    override fun getItemViewType(position: Int): Int = if (isGridLayout) VIEW_GRID else VIEW_LIST

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layout = if (viewType == VIEW_GRID) R.layout.item_product_grid else R.layout.item_product_list
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), position, onItemClick)
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView        = itemView.findViewById(R.id.catalog_item_image)
        private val name: TextView          = itemView.findViewById(R.id.catalog_item_name)
        private val priceCurrent: TextView  = itemView.findViewById(R.id.catalog_item_price_current)
        private val priceOriginal: TextView = itemView.findViewById(R.id.catalog_item_price_original)
        private val badge: TextView         = itemView.findViewById(R.id.catalog_item_discount_badge)

        fun bind(product: Product, position: Int, onClick: (Product) -> Unit) {
            name.text = product.name

            val ctx = itemView.context
            image.setBackgroundColor(ProductImageUtil.backgroundColorFor(product.category))
            image.load(ProductImageUtil.getProductImageRes(ctx, product.category, product.imageIndex)) {
                crossfade(true)
                placeholder(ProductImageUtil.fallbackRes(product.category))
                error(ProductImageUtil.fallbackRes(product.category))
            }

            val discounted = product.discountPercentage > 0
            priceCurrent.text = PriceUtil.formatCents(product.discountedPriceCents)

            if (discounted) {
                priceOriginal.text = PriceUtil.formatCents(product.priceCents)
                priceOriginal.paintFlags = priceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                priceOriginal.visibility = View.VISIBLE
                badge.text = ctx.getString(R.string.discount_badge_format, product.discountPercentage)
                badge.visibility = View.VISIBLE
            } else {
                priceOriginal.visibility = View.GONE
                badge.visibility = View.GONE
            }

            itemView.contentDescription    = "product_${product.id}"
            name.contentDescription        = "product_item_${position}_title"
            priceCurrent.contentDescription  = "catalog_item_${position}_price_current"
            priceOriginal.contentDescription = "catalog_item_${position}_price_original"
            badge.contentDescription         = "catalog_item_${position}_discount_badge"
            itemView.setOnClickListener { onClick(product) }
        }
    }

    companion object {
        private const val VIEW_GRID = 0
        private const val VIEW_LIST = 1

        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(a: Product, b: Product) = a.id == b.id
            override fun areContentsTheSame(a: Product, b: Product) = a == b
        }
    }
}
