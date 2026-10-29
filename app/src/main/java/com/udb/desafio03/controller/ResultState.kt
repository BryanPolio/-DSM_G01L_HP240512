package com.udb.desafio03.controller

// Maneja los 3 estados obligatorios de carga y error
sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val message: String) : ResultState<Nothing>()
}