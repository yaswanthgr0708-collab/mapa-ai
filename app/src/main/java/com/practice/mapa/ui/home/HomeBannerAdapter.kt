package com.practice.mapa.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practice.mapa.databinding.ItemHomeBannerBinding

data class BannerItem(
    val headline: String,
    val subline: String,
    val cta: String,
    val backgroundColorInt: Int,
    val category: String
)

class HomeBannerAdapter(
    private val items: List<BannerItem>,
    private val onCtaClick: (String) -> Unit
) : RecyclerView.Adapter<HomeBannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(val binding: ItemHomeBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemHomeBannerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            root.setCardBackgroundColor(item.backgroundColorInt)
            bannerHeadline.text = item.headline
            bannerSubline.text = item.subline
            bannerCta.text = item.cta
            root.contentDescription = "home_banner_$position"
            bannerCta.contentDescription = "home_banner_${position}_cta"
            bannerCta.setOnClickListener { onCtaClick(item.category) }
            root.setOnClickListener { onCtaClick(item.category) }
        }
    }

    override fun getItemCount() = items.size
}
