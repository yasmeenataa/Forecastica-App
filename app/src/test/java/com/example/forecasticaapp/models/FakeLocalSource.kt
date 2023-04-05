package com.example.forecasticaapp.models

import com.example.forecasticaapp.database.LocalSource
import com.example.forecasticaapp.database.RoomAlertPojo
import com.example.forecasticaapp.database.RoomFavPojo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource(
   private var oneCallResponse: MutableList<OneCallResponse> = mutableListOf(),
    private var alertList: MutableList<RoomAlertPojo> = mutableListOf(),
    private var favList: MutableList<RoomFavPojo> = mutableListOf()
) : LocalSource {
    override suspend fun insertCurrentWeather(weather: OneCallResponse?) {
        if (weather != null) {
            oneCallResponse.add(weather)
        }
    }

    override suspend fun deleteCurrentWeather() {
        oneCallResponse.clear()
    }

    override fun getCurrentWeather(): Flow<List<OneCallResponse>> {
        return flowOf(oneCallResponse)
    }

    override fun getFavWeather(): Flow<List<RoomFavPojo>> {
        return flowOf(favList)
    }

    override suspend fun insertFavWeather(favWeather: RoomFavPojo) {
        favList.add(favWeather)
    }

    override suspend fun deleteFavWeather(favWeather: RoomFavPojo) {
        favList.remove(favWeather)
    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>>{
       return flowOf(alertList)
    }


    override suspend fun insertAlert(alert: RoomAlertPojo) {
        alertList.add(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        alertList.remove(alert)
    }
}