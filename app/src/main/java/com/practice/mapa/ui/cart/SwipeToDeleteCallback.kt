package com.practice.mapa.ui.cart

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.practice.mapa.R

class SwipeToDeleteCallback(
    private val onSwipeDelete: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        @Suppress("DEPRECATION")
        onSwipeDelete(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val background: Drawable = ColorDrawable(
            ContextCompat.getColor(recyclerView.context, R.color.swipe_delete_background)
        )
        val deleteIcon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete_white)

        background.setBounds(
            itemView.right + dX.toInt(), itemView.top,
            itemView.right, itemView.bottom
        )
        background.draw(c)

        if (deleteIcon != null) {
            val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + deleteIcon.intrinsicHeight
            val iconRight = itemView.right - iconMargin
            val iconLeft = iconRight - deleteIcon.intrinsicWidth
            if (dX < -deleteIcon.intrinsicWidth - iconMargin) {
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                deleteIcon.draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
