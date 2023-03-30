package com.example.forecasticaapp.database

import android.content.Context
import com.example.forecasticaapp.models.OneCallResponse
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(context: Context):LocalSource  {

    private val weatherDAO:CurrentWeatherDAO by lazy {
        val db:WeatherDatabase=WeatherDatabase.getInstance(context)
        db.getCurrentWeatherDao()
    }
    override suspend fun insertCurrentWeather(weather: OneCallResponse?) {
       weatherDAO.insert(weather)
    }

    override suspend fun deleteCurrentWeather() {
       weatherDAO.delete()
    }

    override suspend fun getCurrentWeather(): List<OneCallResponse> {
        return weatherDAO.getCurrentWeather()
    }

//    override  fun getCurrentWeather(): Flow<OneCallResponse> {
//        return weatherDAO.getCurrentWeather()
//    }
}