package com.example.forecasticaapp.models

import com.example.forecasticaapp.database.RoomAlertPojo
import com.example.forecasticaapp.database.RoomFavPojo
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getOneCallResponse(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<OneCallResponse>

      fun getCurrentWeather(): Flow<List<RoomHomePojo>>
    suspend fun deleteCurrentWeather()
    suspend fun insertCurrentWeather(weather: RoomHomePojo?)

    fun  getFavWeather(): Flow<List<RoomFavPojo>>
    suspend fun insertFavWeather(favWeather: RoomFavPojo)
    suspend fun deleteFavWeather(favWeather: RoomFavPojo)

    fun getAllAlerts(): Flow<List<RoomAlertPojo>>
    suspend fun  insertAlert(alert: RoomAlertPojo)
    suspend  fun deleteAlert(alert: RoomAlertPojo)
}