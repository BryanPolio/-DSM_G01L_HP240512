package com.udb.desafio03.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.udb.desafio03.BuildConfig
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    }

    // Configuración REST Countries token
    private val countriesClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) Desafio03/1.0")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer ${BuildConfig.REST_COUNTRIES_TOKEN.trim()}")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(logging)
        .build()

    val countriesApi: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.restcountries.com/")
        .client(countriesClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Cliente para GeoNames y WeatherAPI
    private val generalClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build()

    // Configuración para GeoNames
    val geoNamesApi: Retrofit = Retrofit.Builder()
        .baseUrl("https://secure.geonames.org/")
        .client(generalClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Configuración para WeatherAPI
    val weatherApi: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .client(generalClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
