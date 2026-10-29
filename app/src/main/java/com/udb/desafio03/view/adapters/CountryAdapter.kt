package com.udb.desafio03.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.udb.desafio03.databinding.ItemCountryBinding
import com.udb.desafio03.model.Country

class CountryAdapter(
    private var countries: List<Country>,
    private val onClick: (Country) -> Unit
) : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(country: Country) {
            binding.tvCountryName.text = country.name.common
            binding.tvCapital.text = country.capital?.firstOrNull() ?: "Sin capital"

            // Carga asíncrona de la bandera
            binding.ivFlag.load(country.flags.png) {
                crossfade(true)
            }
            binding.root.setOnClickListener { onClick(country) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(countries[position])
    override fun getItemCount() = countries.size

    fun updateData(newCountries: List<Country>) {
        countries = newCountries
        notifyDataSetChanged()
    }
}