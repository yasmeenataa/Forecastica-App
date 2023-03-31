package com.example.forecasticaapp.database

import androidx.room.*
import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.RoomHomePojo
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDAO {
    @Query("Select * from CurrentWeather")
     fun  getCurrentWeather(): Flow<List<RoomHomePojo>>
//    @Query("Select * from CurrentWeather where id=1")
//    fun getCurrentWeather(): Flow<OneCallResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather: RoomHomePojo?)
    @Query("Delete from CurrentWeather")
    suspend fun deleteCurrentWeather()
}