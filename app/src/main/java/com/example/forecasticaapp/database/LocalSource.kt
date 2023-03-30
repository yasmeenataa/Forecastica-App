package com.example.forecasticaapp.database

import com.example.forecasticaapp.models.OneCallResponse
import kotlinx.coroutines.flow.Flow


interface LocalSource {
    suspend fun insertCurrentWeather(weather: OneCallResponse?)
    suspend fun deleteCurrentWeather()
     suspend fun getCurrentWeather(): List<OneCallResponse>
}