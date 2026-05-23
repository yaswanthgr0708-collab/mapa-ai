package com.practice.mapa.ui.catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.practice.mapa.R
import com.practice.mapa.data.catalog.Product
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
        private val image: ImageView = itemView.findViewById(R.id.catalog_item_image)
        private val name: TextView = itemView.findViewById(R.id.catalog_item_name)
        private val price: TextView = itemView.findViewById(R.id.catalog_item_price)

        fun bind(product: Product, position: Int, onClick: (Product) -> Unit) {
            name.text = product.name
            price.text = "$%.2f".format(product.price)
            image.setImageResource(ProductImageUtil.imageResFor(product.category))
            image.setBackgroundColor(ProductImageUtil.backgroundColorFor(product.category))
            // Root: stable product-id-based id so tests can find "product 42" directly.
            // Child fields: position-indexed per HANDOFF Section 4.3.
            itemView.contentDescription = "product_${product.id}"
            name.contentDescription  = "product_item_${position}_title"
            price.contentDescription = "product_item_${position}_price"
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
