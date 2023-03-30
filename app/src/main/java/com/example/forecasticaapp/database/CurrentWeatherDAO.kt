package com.example.forecasticaapp.database

import androidx.room.*
import com.example.forecasticaapp.models.OneCallResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDAO {
    @Query("Select * from CurrentWeather")
    suspend fun  getCurrentWeather(): List<OneCallResponse>
//    @Query("Select * from CurrentWeather where id=1")
//    fun getCurrentWeather(): Flow<OneCallResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: OneCallResponse?)
    @Query("Delete from CurrentWeather")
    suspend fun delete()
}