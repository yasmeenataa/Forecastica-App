package com.example.forecasticaapp.database

import androidx.room.*
import com.example.forecasticaapp.models.RoomHomePojo
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteWeatherDAO {
    @Query("Select * from FavoriteWeather")
    fun  getFavWeather(): Flow<List<RoomFavPojo>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavWeather(favWeather:RoomFavPojo)
    @Delete
    suspend fun deleteFavWeather(favWeather:RoomFavPojo)
}