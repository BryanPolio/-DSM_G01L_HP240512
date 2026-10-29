package com.udb.desafio03.view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.udb.desafio03.databinding.ItemCityBinding
import com.udb.desafio03.model.GeoCity
import com.udb.desafio03.model.WeatherResponse
import java.text.NumberFormat
import java.util.Locale

class CityAdapter(
    private var citiesWeather: List<Pair<GeoCity, WeatherResponse?>>,
    private val onCityClick: (GeoCity, WeatherResponse?) -> Unit
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    inner class ViewHolder(val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<GeoCity, WeatherResponse?>) {
            val city = item.first
            val weather = item.second

            // Nombre de la ciudad
            binding.tvCityName.text = city.name

            // Destacar capital del país (código PPLC en GeoNames)
            val isCapital = city.fcode == "PPLC"
            if (isCapital) {
                binding.tvCapitalBadge.visibility = View.VISIBLE
                binding.cardCity.setCardBackgroundColor(Color.parseColor("#FFFBEB")) // Amarillo suave
                binding.cardCity.strokeColor = Color.parseColor("#FCD34D")
            } else {
                binding.tvCapitalBadge.visibility = View.GONE
                binding.cardCity.setCardBackgroundColor(Color.WHITE)
                binding.cardCity.strokeColor = Color.parseColor("#E2E8F0")
            }

            // Datos de GeoNames: Población y coordenadas
            val popFormatted = try {
                numberFormat.format(city.population)
            } catch (e: Exception) {
                city.population.toString()
            }
            binding.tvPopulation.text = "👥 Población: $popFormatted hab."
            binding.tvCoordinates.text = "📍 ${city.lat}, ${city.lng}"

            // Datos de WeatherAPI
            if (weather != null) {
                binding.layoutWeatherInfo.visibility = View.VISIBLE
                binding.tvWeatherUnavailable.visibility = View.GONE

                // Temperatura en °C y °F
                binding.tvTemp.text = "${weather.current.temp_c}°C / ${weather.current.temp_f}°F"

                // Condición climática
                binding.tvCondition.text = weather.current.condition.text

                // Velocidad de viento y humedad
                binding.tvWeatherDetails.text = "💨 Viento: ${weather.current.wind_kph} km/h (${weather.current.wind_mph} mph) • 💧 Humedad: ${weather.current.humidity}%"

                // Fecha y hora de última actualización
                binding.tvLastUpdated.text = "🕒 ${weather.current.last_updated}"

                // Ícono de estado del clima
                val iconUrl = if (weather.current.condition.icon.startsWith("//")) {
                    "https:${weather.current.condition.icon}"
                } else {
                    weather.current.condition.icon
                }
                binding.ivWeatherIcon.load(iconUrl) {
                    crossfade(true)
                }
            } else {
                // Si el clima falla puntualmente para esta ciudad, no bloquea la interfaz
                binding.layoutWeatherInfo.visibility = View.GONE
                binding.tvWeatherUnavailable.visibility = View.VISIBLE
                binding.tvTemp.text = "N/A"
            }

            binding.root.setOnClickListener { onCityClick(city, weather) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(citiesWeather[position])
    override fun getItemCount() = citiesWeather.size

    fun updateData(newData: List<Pair<GeoCity, WeatherResponse?>>) {
        citiesWeather = newData
        notifyDataSetChanged()
    }
}
