package com.practice.mapa.ui.cart

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

class CartAdapter(
    private val onIncrease: (Int) -> Unit,
    private val onDecrease: (Int) -> Unit
) : ListAdapter<CartItemWithProduct, CartAdapter.CartViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position), onIncrease, onDecrease)
    }

    fun getItemAt(position: Int): CartItemWithProduct = getItem(position)

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView       = itemView.findViewById(R.id.cart_item_image)
        private val name: TextView         = itemView.findViewById(R.id.cart_item_name)
        private val price: TextView        = itemView.findViewById(R.id.cart_item_price)
        private val btnDecrease: ImageButton = itemView.findViewById(R.id.cart_item_button_decrease)
        private val qty: TextView          = itemView.findViewById(R.id.cart_item_text_quantity)
        private val btnIncrease: ImageButton = itemView.findViewById(R.id.cart_item_button_increase)
        private val total: TextView        = itemView.findViewById(R.id.cart_item_text_total)

        fun bind(item: CartItemWithProduct, onIncrease: (Int) -> Unit, onDecrease: (Int) -> Unit) {
            val product = item.product
            name.text = product.name
            price.text = "$%.2f".format(product.price)
            qty.text = item.cartItem.quantity.toString()
            total.text = "$%.2f".format(product.price * item.cartItem.quantity)

            image.load(categoryPlaceholder(product.category))
            itemView.contentDescription = "cart_item_${product.id}"

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
