package com.udb.desafio03.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.udb.desafio03.databinding.ActivityMainBinding
import com.udb.desafio03.view.adapters.RegionAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val regions = listOf("Africa", "Americas", "Asia", "Europe", "Oceania")

        binding.rvRegions.layoutManager = LinearLayoutManager(this)
        binding.rvRegions.adapter = RegionAdapter(regions) { regionSeleccionada ->
            val intent = Intent(this, CountriesActivity::class.java).apply {
                putExtra("REGION", regionSeleccionada)
            }
            startActivity(intent)
        }
    }
}