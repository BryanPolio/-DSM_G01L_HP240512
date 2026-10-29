package com.udb.desafio03.view.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.udb.desafio03.R
import com.udb.desafio03.controller.CityWeatherController
import com.udb.desafio03.controller.ResultState
import com.udb.desafio03.databinding.ActivityDetailBinding
import com.udb.desafio03.model.Country
import com.udb.desafio03.view.adapters.CityAdapter
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDetailBinding
    private val controller = CityWeatherController()
    private lateinit var adapter: CityAdapter
    private var mMap: GoogleMap? = null
    private val cityMarkers = mutableMapOf<String, Marker>()
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val country = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("COUNTRY_EXTRA", Country::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("COUNTRY_EXTRA") as? Country
        }

        val countryIso = intent.getStringExtra("ISO2") ?: country?.cca2 ?: return
        val countryName = intent.getStringExtra("COUNTRY_NAME") ?: country?.name?.common ?: "País"

        title = "Detalles: $countryName"


        if (country != null) {
            setupCountrySpecs(country)
        } else {
            binding.cardCountryInfo.visibility = View.GONE
        }

        // Inicializar Mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        adapter = CityAdapter(emptyList()) { city, _ ->
            // Al hacer clic en una ciudad, centrar el mapa y mostrar su marcador
            try {
                val latLng = LatLng(city.lat.toDouble(), city.lng.toDouble())
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))
                cityMarkers[city.name]?.showInfoWindow()
            } catch (e: Exception) {

            }
        }

        binding.rvCities.layoutManager = LinearLayoutManager(this)
        binding.rvCities.adapter = adapter

        fetchCitiesAndWeather(countryIso)
    }

    private fun setupCountrySpecs(country: Country) {
        binding.tvDetailCountryName.text = country.name.common
        binding.tvDetailOfficialName.text = country.name.official

        // Códigos ISO (Alpha-2 y Alpha-3)                                    INFORMACION DE ARRIBA
        val isoText = buildString {
            append("Códigos ISO: Alpha-2 [${country.cca2}]")
            if (country.cca3.isNotBlank()) {
                append(" • Alpha-3 [${country.cca3}]")
            }
        }
        binding.tvDetailIso.text = isoText

        // Bandera
        binding.ivDetailFlag.load(country.flags.png) {
            crossfade(true)
        }

        // Capital
        val cap = country.capital?.firstOrNull() ?: "Sin capital"
        binding.tvDetailCapital.text = "Capital: $cap"


        binding.tvDetailRegion.text = "Región: ${country.region} • Subregión: ${country.subregion ?: "N/A"}"


        val pop = try {
            numberFormat.format(country.population)
        } catch (e: Exception) {
            country.population.toString()
        }
        binding.tvDetailPopulation.text = "Población: $pop habitantes"


        binding.tvDetailCurrencies.text = "Monedas: ${country.currenciesText}"

        binding.tvDetailLanguages.text = "Idiomas: ${country.languagesText}"

        val coords = if (country.latlng != null && country.latlng.size >= 2) {
            "${country.latlng[0]}, ${country.latlng[1]}"
        } else {
            "No especificadas"
        }
        binding.tvDetailCoordinates.text = "Coordenadas: $coords"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun fetchCitiesAndWeather(iso2: String) {
        lifecycleScope.launch {
            binding.progressBarDetail.visibility = View.VISIBLE

            when (val result = controller.getCitiesAndWeather(iso2)) {
                is ResultState.Success -> {
                    binding.progressBarDetail.visibility = View.GONE
                    adapter.updateData(result.data)

                    // Colocar marcadores en el mapa para cada ciudad
                    mMap?.let { map ->
                        map.clear()
                        cityMarkers.clear()

                        result.data.forEach { (city, weather) ->
                            try {
                                val latLng = LatLng(city.lat.toDouble(), city.lng.toDouble())
                                val title = if (city.fcode == "PPLC") "⭐ ${city.name} (Capital)" else city.name
                                val snippet = if (weather != null) {
                                    "${weather.current.temp_c}°C / ${weather.current.temp_f}°F - ${weather.current.condition.text}"
                                } else {
                                    "Clima no disponible"
                                }

                                val marker = map.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(title)
                                        .snippet(snippet)
                                )
                                if (marker != null) {
                                    cityMarkers[city.name] = marker
                                }
                            } catch (e: Exception) {
                                // Evitar fallos si una ciudad tiene lat/lng no numérico
                            }
                        }

                        // Centrar la cámara en la primera ciudad
                        if (result.data.isNotEmpty()) {
                            val firstCity = result.data.first().first
                            try {
                                val firstLatLng = LatLng(firstCity.lat.toDouble(), firstCity.lng.toDouble())
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 6f))
                            } catch (e: Exception) {

                            }
                        }
                    }
                }
                is ResultState.Error -> {
                    binding.progressBarDetail.visibility = View.GONE
                    Toast.makeText(this@DetailActivity, result.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }
}
