package com.example.forecasticaapp.database

import android.content.Context
import com.example.forecasticaapp.models.RoomHomePojo
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(context: Context):LocalSource  {

    private val weatherDAO:CurrentWeatherDAO by lazy {
        val db:WeatherDatabase=WeatherDatabase.getInstance(context)
        db.getCurrentWeatherDao()
    }
    private val favoriteDAO:FavoriteWeatherDAO by lazy {
        val db:WeatherDatabase=WeatherDatabase.getInstance(context)
        db.getFavoriteWeatherDao()
    }
    override suspend fun insertCurrentWeather(weather: RoomHomePojo?) {
       weatherDAO.insertCurrentWeather(weather)
    }

    override suspend fun deleteCurrentWeather() {
       weatherDAO.deleteCurrentWeather()
    }

    override  fun getCurrentWeather(): Flow<List<RoomHomePojo>> {
        return weatherDAO.getCurrentWeather()
    }

    override fun getFavWeather(): Flow<List<RoomFavPojo>> {
       return favoriteDAO.getFavWeather()
    }

    override suspend fun insertFavWeather(favWeather: RoomFavPojo) {
        favoriteDAO.insertFavWeather(favWeather)
    }

    override suspend fun deleteFavWeather(favWeather: RoomFavPojo) {
        favoriteDAO.deleteFavWeather(favWeather)
    }

//    override  fun getCurrentWeather(): Flow<OneCallResponse> {
//        return weatherDAO.getCurrentWeather()
//    }
}