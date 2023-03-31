package com.example.forecasticaapp.database


import com.example.forecasticaapp.models.RoomHomePojo
import kotlinx.coroutines.flow.Flow


interface LocalSource {
    suspend fun insertCurrentWeather(weather: RoomHomePojo?)
    suspend fun deleteCurrentWeather()
      fun getCurrentWeather(): Flow<List<RoomHomePojo>>
    fun  getFavWeather(): Flow<List<RoomFavPojo>>
    suspend fun insertFavWeather(favWeather:RoomFavPojo)
    suspend fun deleteFavWeather(favWeather:RoomFavPojo)
}