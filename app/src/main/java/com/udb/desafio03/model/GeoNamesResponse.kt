package com.udb.desafio03.model

data class GeoNamesResponse(
    val geonames: List<GeoCity>?,
    val status: GeoStatus? = null
)

data class GeoStatus(
    val message: String?,
    val value: Int?
)

data class GeoCity(
    val name: String,
    val population: Long,
    val lat: String,
    val lng: String,
    val fcode: String? // Si el fcode es "PPLC", significa que es la capital del país
)
