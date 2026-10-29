package com.udb.desafio03.api

import com.udb.desafio03.BuildConfig
import com.udb.desafio03.model.RestCountriesResponse
import com.udb.desafio03.model.GeoNamesResponse
import com.udb.desafio03.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CountriesService {
    @GET("countries/v5")
    suspend fun getAllCountries(
        @Query("limit") limit: Int = 100
    ): Response<RestCountriesResponse>

    @GET("countries/v5")
    suspend fun getCountriesByRegion(
        @Query("region") region: String,
        @Query("limit") limit: Int = 100
    ): Response<RestCountriesResponse>
}

interface GeoNamesService {
    @GET("searchJSON")
    suspend fun getCities(
        @Query("country") countryIso: String,
        @Query("featureClass") featureClass: String = "P",
        @Query("cities") cities: String = "cities15000",
        @Query("maxRows") maxRows: Int = 10,
        @Query("orderby") orderBy: String = "population",
        @Query("username") username: String = BuildConfig.GEONAMES_USER
    ): Response<GeoNamesResponse>
}

interface WeatherService {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String = BuildConfig.WEATHER_API_KEY,
        @Query("q") latLng: String,
        @Query("lang") lang: String = "es"
    ): Response<WeatherResponse>
}
