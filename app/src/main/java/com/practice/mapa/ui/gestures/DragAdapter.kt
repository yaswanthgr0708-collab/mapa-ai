package com.practice.mapa.ui.gestures

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practice.mapa.R

class DragAdapter(private val items: MutableList<String>) :
    RecyclerView.Adapter<DragAdapter.ViewHolder>() {

    class ViewHolder(val text: TextView) : RecyclerView.ViewHolder(text)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drag, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position]
        holder.text.contentDescription = "drag_item_$position"
    }

    override fun getItemCount() = items.size

    fun moveItem(from: Int, to: Int) {
        val item = items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
    }
}
