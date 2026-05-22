package com.practice.mapa.ui.gestures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practice.mapa.R

class ScrollAdapter : RecyclerView.Adapter<ScrollAdapter.ViewHolder>() {

    class ViewHolder(val text: TextView) : RecyclerView.ViewHolder(text)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scroll, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemNumber = position + 1
        holder.text.text = holder.text.context.getString(R.string.gestures_scroll_item_format, itemNumber)

        if (position == 86) {
            holder.text.id = R.id.gestures_scroll_target_item
            holder.text.contentDescription = "gestures_scroll_target_item"
        } else {
            holder.text.id = View.NO_ID
            holder.text.contentDescription = "gestures_scroll_item_$itemNumber"
        }
    }

    override fun getItemCount() = 100
}
