package com.example.newsbara.presentation.common

sealed class ResultState<out T> {
    object Idle : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Failure(val message: String) : ResultState<Nothing>()
}