package com.example.forecasticaapp.database

import androidx.room.*
import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.RoomHomePojo
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDAO {
    @Query("Select * from CurrentWeather")
     fun  getCurrentWeather(): Flow<List<OneCallResponse>>
//    @Query("Select * from CurrentWeather where id=1")
//    fun getCurrentWeather(): Flow<OneCallResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather: OneCallResponse?)
    @Query("Delete from CurrentWeather")
    suspend fun deleteCurrentWeather()
}
@Dao
interface FavoriteWeatherDAO {
    @Query("Select * from FavoriteWeather")
    fun  getFavWeather(): Flow<List<RoomFavPojo>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavWeather(favWeather:RoomFavPojo)
    @Delete
    suspend fun deleteFavWeather(favWeather:RoomFavPojo)
}

@Dao
interface AlertDAO {
    @Query("Select * from AlertTable")
    fun  getAllAlerts(): Flow<List<RoomAlertPojo>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert:RoomAlertPojo)
    @Delete
    suspend fun deleteAlert(alert:RoomAlertPojo)
}