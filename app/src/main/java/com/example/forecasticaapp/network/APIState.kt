package com.example.forecasticaapp.network

import com.example.forecasticaapp.models.OneCallResponse


sealed class APIState {
    class Success(val data:OneCallResponse): APIState()
    class Failure(val msg:Throwable): APIState()
    object Loading: APIState()
}