package com.udb.desafio03.model

data class WeatherResponse(
    val current: CurrentWeather
)

data class CurrentWeather(
    val temp_c: Double,
    val temp_f: Double,
    val condition: WeatherCondition,
    val wind_kph: Double,
    val wind_mph: Double,
    val humidity: Int,
    val last_updated: String
)

data class WeatherCondition(val text: String, val icon: String)