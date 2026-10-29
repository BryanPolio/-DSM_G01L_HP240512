package com.udb.desafio03.model

import java.io.Serializable

// Respuesta raíz de la API REST Countries v5
data class RestCountriesResponse(
    val data: RestCountriesData?
) : Serializable

data class RestCountriesData(
    val objects: List<CountryV5>?
) : Serializable

data class CountryV5(
    val names: V5Names?,
    val codes: V5Codes?,
    val capitals: List<V5Capital>?,
    val flag: V5Flag?,
    val region: String?,
    val subregion: String?,
    val population: Long?,
    val coordinates: V5Coordinates?,
    val currencies: List<V5Currency>?,
    val languages: List<V5Language>?
) : Serializable {
    // Mapea la estructura v5 al modelo de dominio Country que usa la app
    fun toDomainCountry(): Country {

        val spanishCommon = names?.translations?.spa?.common?.trim()?.takeIf { it.isNotEmpty() }
        val englishCommon = names?.common?.trim()?.takeIf { it.isNotEmpty() } ?: "Desconocido"

        // Mostrar nombre en español como principal para que España aparezca bajo la E, Alemania bajo la A, etc.
        val displayName = if (spanishCommon != null && !spanishCommon.equals(englishCommon, ignoreCase = true)) {
            "$spanishCommon ($englishCommon)"
        } else {
            spanishCommon ?: englishCommon
        }

        val officialName = names?.translations?.spa?.official ?: names?.official ?: displayName
        val alpha2 = codes?.alpha_2 ?: ""
        val alpha3 = codes?.alpha_3 ?: ""
        val capitalList = capitals?.mapNotNull { it.name } ?: emptyList()
        val flagPng = flag?.url_png ?: ""
        val flagSvg = flag?.url_svg ?: ""
        val latLngList = if (coordinates != null) listOf(coordinates.lat, coordinates.lng) else null

        val currText = currencies?.mapNotNull { curr ->
            val n = curr.name ?: curr.code
            val s = curr.symbol
            if (n != null && !s.isNullOrBlank()) "$n ($s)" else n
        }?.joinToString(", ")?.ifBlank { "No especificada" } ?: "No especificada"

        val langText = languages?.mapNotNull { it.name }?.joinToString(", ")?.ifBlank { "No especificado" } ?: "No especificado"

        return Country(
            name = Name(common = displayName, official = officialName),
            capital = capitalList,
            region = region ?: "Desconocida",
            subregion = subregion ?: "N/A",
            population = population ?: 0L,
            flags = Flags(png = flagPng, svg = flagSvg),
            cca2 = alpha2,
            cca3 = alpha3,
            currenciesText = currText,
            languagesText = langText,
            latlng = latLngList
        )
    }
}

data class V5Names(
    val common: String?,
    val official: String?,
    val translations: V5Translations?
) : Serializable

data class V5Translations(
    val spa: V5Translation?
) : Serializable

data class V5Translation(
    val common: String?,
    val official: String?
) : Serializable

data class V5Codes(
    val alpha_2: String?,
    val alpha_3: String?
) : Serializable

data class V5Capital(
    val name: String?
) : Serializable

data class V5Flag(
    val url_png: String?,
    val url_svg: String?
) : Serializable

data class V5Coordinates(
    val lat: Double,
    val lng: Double
) : Serializable

data class V5Currency(
    val code: String?,
    val name: String?,
    val symbol: String?
) : Serializable

data class V5Language(
    val name: String?,
    val native_name: String?
) : Serializable

// Modelo de dominio utilizado por CountryAdapter y las actividades
data class Country(
    val name: Name,
    val capital: List<String>?,
    val region: String,
    val subregion: String?,
    val population: Long,
    val flags: Flags,
    val cca2: String, // Código ISO alpha-2, vital para conectar con GeoNames
    val cca3: String = "", // Código ISO alpha-3
    val currenciesText: String = "",
    val languagesText: String = "",
    val latlng: List<Double>?,
    val currencies: Map<String, Currency>? = null,
    val languages: Map<String, String>? = null
) : Serializable

data class Name(val common: String, val official: String) : Serializable
data class Flags(val png: String, val svg: String) : Serializable
data class Currency(val name: String, val symbol: String) : Serializable
