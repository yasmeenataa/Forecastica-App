package com.example.forecasticaapp.network


sealed class ResponseState<out T> {
    class Success<T>(val data:T): ResponseState<T>()
    class Failure(val msg:Throwable): ResponseState<Nothing>()
    object Loading: ResponseState<Nothing>()
}