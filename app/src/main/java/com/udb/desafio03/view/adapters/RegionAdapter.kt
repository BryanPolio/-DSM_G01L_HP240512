package com.udb.desafio03.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udb.desafio03.databinding.ItemRegionBinding

class RegionAdapter(
    private val regions: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<RegionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRegionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(region: String) {
            binding.tvRegionName.text = region
            binding.root.setOnClickListener { onClick(region) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(regions[position])
    override fun getItemCount() = regions.size
}