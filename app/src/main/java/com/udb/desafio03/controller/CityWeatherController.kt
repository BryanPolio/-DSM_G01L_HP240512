package com.udb.desafio03.controller

import com.udb.desafio03.api.GeoNamesService
import com.udb.desafio03.api.RetrofitClient
import com.udb.desafio03.api.WeatherService
import com.udb.desafio03.model.GeoCity
import com.udb.desafio03.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CityWeatherController {
    private val geoApi = RetrofitClient.geoNamesApi.create(GeoNamesService::class.java)
    private val weatherApi = RetrofitClient.weatherApi.create(WeatherService::class.java)

    suspend fun getCitiesAndWeather(countryIso: String): ResultState<List<Pair<GeoCity, WeatherResponse?>>> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Obtener ciudades desde GeoNames
                val geoResponse = geoApi.getCities(countryIso = countryIso)

                if (!geoResponse.isSuccessful) {
                    val code = geoResponse.code()
                    val msg = when (code) {
                        401 -> "Usuario de GeoNames inválido o no autorizado"
                        403 -> "Acceso denegado a los servicios de GeoNames"
                        else -> "Error $code al consultar GeoNames"
                    }
                    return@withContext ResultState.Error(msg)
                }

                val body = geoResponse.body()
                if (body == null) {
                    return@withContext ResultState.Error("Respuesta vacía de GeoNames")
                }


                if (body.status != null) {
                    val statusMsg = body.status.message ?: "Límite de solicitudes alcanzado en GeoNames"
                    return@withContext ResultState.Error("GeoNames: $statusMsg")
                }

                val cities = body.geonames
                if (cities.isNullOrEmpty()) {
                    return@withContext ResultState.Error("País sin ciudades registradas en GeoNames")
                }

                // 2. Obtener clima de cada ciudad simultáneamente
                val citiesWithWeather = cities.take(10).map { city ->
                    async {
                        try {
                            val weatherRes = weatherApi.getCurrentWeather(latLng = "${city.lat},${city.lng}")
                            if (weatherRes.isSuccessful && weatherRes.body() != null) {
                                Pair(city, weatherRes.body())
                            } else {
                                Pair(city, null) // Si falla puntualmente esta ciudad, no bloquea el resto
                            }
                        } catch (e: Exception) {
                            Pair(city, null) // Falta de conexión o timeout puntual para esta ciudad
                        }
                    }
                }.awaitAll()

                ResultState.Success(citiesWithWeather)
            } catch (e: SocketTimeoutException) {
                ResultState.Error("Tiempo de espera agotado al conectar con GeoNames")
            } catch (e: UnknownHostException) {
                ResultState.Error("Falta de conexión a Internet")
            } catch (e: Exception) {
                ResultState.Error("Error al consultar GeoNames: ${e.message}")
            }
        }
    }
}
