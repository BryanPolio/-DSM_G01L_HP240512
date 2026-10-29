package com.udb.desafio03.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.udb.desafio03.controller.CountryController
import com.udb.desafio03.controller.ResultState
import com.udb.desafio03.databinding.ActivityCountriesBinding
import com.udb.desafio03.model.Country
import com.udb.desafio03.view.adapters.CountryAdapter
import kotlinx.coroutines.launch
import java.util.Locale

class CountriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCountriesBinding
    private val controller = CountryController()
    private lateinit var adapter: CountryAdapter
    private var allCountries: List<Country> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val region = intent.getStringExtra("REGION") ?: return
        title = "Países de $region"

        adapter = CountryAdapter(emptyList()) { country ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("COUNTRY_EXTRA", country)
                putExtra("ISO2", country.cca2)
                putExtra("COUNTRY_NAME", country.name.common)
            }
            startActivity(intent)
        }

        binding.rvCountries.layoutManager = LinearLayoutManager(this)
        binding.rvCountries.adapter = adapter

        // Filtrado en tiempo real con el buscador
        binding.etSearchCountry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCountries(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        fetchCountries(region)
    }

    private fun filterCountries(query: String) {
        val cleanQuery = query.trim().lowercase(Locale.getDefault())
        val filtered = if (cleanQuery.isEmpty()) {
            allCountries
        } else {
            allCountries.filter { country ->
                country.name.common.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                country.name.official.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                country.cca2.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                country.cca3.lowercase(Locale.getDefault()).contains(cleanQuery)
            }
        }
        adapter.updateData(filtered)
        binding.tvNoResults.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun fetchCountries(region: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvNoResults.visibility = View.GONE

            when (val result = controller.getCountriesByRegion(region)) {
                is ResultState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    allCountries = result.data
                    adapter.updateData(allCountries)
                    binding.tvNoResults.visibility = if (allCountries.isEmpty()) View.VISIBLE else View.GONE
                }
                is ResultState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@CountriesActivity, result.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }
}
