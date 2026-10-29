package com.udb.desafio03.controller

import com.udb.desafio03.api.CountriesService
import com.udb.desafio03.api.RetrofitClient
import com.udb.desafio03.model.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CountryController {
    private val api = RetrofitClient.countriesApi.create(CountriesService::class.java)

    suspend fun getCountriesByRegion(region: String): ResultState<List<Country>> {
        return withContext(Dispatchers.IO) {
            try {
                // Solicitar con limit recibir todos los países de la región sin cortes
                val response = api.getCountriesByRegion(region = region, limit = 100)
                if (response.isSuccessful && response.body() != null) {
                    val objects = response.body()?.data?.objects ?: emptyList()
                    val countries = objects
                        .map { it.toDomainCountry() }
                        .distinctBy { it.cca2 }
                        .sortedBy { it.name.common }

                    ResultState.Success(countries)
                } else {
                    val code = response.code()
                    val msg = when (code) {
                        401 -> "Token de REST Countries inválido (Error 401)"
                        403 -> "Acceso denegado en REST Countries (Error 403)"
                        429 -> "Límite de solicitudes alcanzado en REST Countries (Error 429)"
                        503 -> "Servidor de REST Countries ocupado temporalmente (Error 503). Reintenta."
                        else -> "Error $code al consultar REST Countries"
                    }
                    ResultState.Error(msg)
                }
            } catch (e: SocketTimeoutException) {
                ResultState.Error("Tiempo de espera agotado al consultar REST Countries. Revisa tu conexión.")
            } catch (e: UnknownHostException) {
                ResultState.Error("Falta de conexión a Internet.")
            } catch (e: Exception) {
                ResultState.Error("Error al procesar países: ${e.message}")
            }
        }
    }
}
