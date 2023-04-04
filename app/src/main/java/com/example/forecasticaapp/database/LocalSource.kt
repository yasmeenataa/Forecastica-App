package com.example.forecasticaapp.database


import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.RoomHomePojo
import kotlinx.coroutines.flow.Flow


interface LocalSource {
    suspend fun insertCurrentWeather(weather: OneCallResponse?)
    suspend fun deleteCurrentWeather()
      fun getCurrentWeather(): Flow<List<OneCallResponse>>
    fun  getFavWeather(): Flow<List<RoomFavPojo>>
    suspend fun insertFavWeather(favWeather:RoomFavPojo)
    suspend fun deleteFavWeather(favWeather:RoomFavPojo)
    fun getAllAlerts(): Flow<List<RoomAlertPojo>>
   suspend fun  insertAlert(alert:RoomAlertPojo)
   suspend  fun deleteAlert(alert:RoomAlertPojo)
}