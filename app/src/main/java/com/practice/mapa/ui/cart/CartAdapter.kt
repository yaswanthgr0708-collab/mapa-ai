package com.practice.mapa.ui.cart

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.practice.mapa.R
import com.practice.mapa.data.catalog.CartItemWithProduct
import com.practice.mapa.util.PriceUtil

class CartAdapter(
    private val onIncrease: (Int) -> Unit,
    private val onDecrease: (Int) -> Unit
) : ListAdapter<CartItemWithProduct, CartAdapter.CartViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position), position, onIncrease, onDecrease)
    }

    fun getItemAt(position: Int): CartItemWithProduct = getItem(position)

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView          = itemView.findViewById(R.id.cart_item_image)
        private val name: TextView            = itemView.findViewById(R.id.cart_item_name)
        private val priceCurrent: TextView    = itemView.findViewById(R.id.cart_item_price_current)
        private val priceOriginal: TextView   = itemView.findViewById(R.id.cart_item_price_original)
        private val btnDecrease: ImageButton  = itemView.findViewById(R.id.cart_item_button_decrease)
        private val qty: TextView             = itemView.findViewById(R.id.cart_item_text_quantity)
        private val btnIncrease: ImageButton  = itemView.findViewById(R.id.cart_item_button_increase)
        private val total: TextView           = itemView.findViewById(R.id.cart_item_text_total)

        fun bind(
            item: CartItemWithProduct,
            position: Int,
            onIncrease: (Int) -> Unit,
            onDecrease: (Int) -> Unit
        ) {
            val product = item.product
            name.text = product.name
            qty.text = item.cartItem.quantity.toString()

            val lineTotalCents = product.discountedPriceCents * item.cartItem.quantity
            priceCurrent.text = PriceUtil.formatCents(product.discountedPriceCents)
            total.text = PriceUtil.formatCents(lineTotalCents)

            val discounted = product.discountPercentage > 0
            if (discounted) {
                priceOriginal.text = PriceUtil.formatCents(product.priceCents)
                priceOriginal.paintFlags = priceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                priceOriginal.visibility = View.VISIBLE
            } else {
                priceOriginal.visibility = View.GONE
            }

            image.load(categoryPlaceholder(product.category))
            itemView.contentDescription = "cart_item_${product.id}"
            priceCurrent.contentDescription  = "cart_row_${position}_price_current"
            priceOriginal.contentDescription = "cart_row_${position}_price_original"

            btnDecrease.isEnabled = item.cartItem.quantity > 1
            btnDecrease.setOnClickListener { onDecrease(product.id) }
            btnIncrease.setOnClickListener { onIncrease(product.id) }
        }

        private fun categoryPlaceholder(category: String) = when (category) {
            "Electronics" -> R.drawable.ic_category_electronics
            "Clothing"    -> R.drawable.ic_category_clothing
            "Books"       -> R.drawable.ic_category_books
            else          -> R.drawable.ic_category_home
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CartItemWithProduct>() {
            override fun areItemsTheSame(a: CartItemWithProduct, b: CartItemWithProduct) =
                a.cartItem.productId == b.cartItem.productId
            override fun areContentsTheSame(a: CartItemWithProduct, b: CartItemWithProduct) = a == b
        }
    }
}
