package com.example.forecasticaapp.models

import com.example.forecasticaapp.database.RoomAlertPojo
import com.example.forecasticaapp.database.RoomFavPojo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository(
    private var fakeRemoteSource: FakeRemoteSource,
    private var fakeLocalSource: FakeLocalSource
) : RepositoryInterface {

    override suspend fun getOneCallResponse(
        lat: Double?,
        lon: Double?,
        units: String?,
        lang: String?
    ): Flow<OneCallResponse> {
        return flowOf( fakeRemoteSource.getOneCallResponse(lat,lon,units,lang))
    }

    override fun getCurrentWeather(): Flow<List<OneCallResponse>> {
        return fakeLocalSource.getCurrentWeather()
    }

    override suspend fun deleteCurrentWeather() {
        fakeLocalSource.deleteCurrentWeather()
    }

    override suspend fun insertCurrentWeather(weather: OneCallResponse?) {
        fakeLocalSource.insertCurrentWeather(weather)
    }

    override fun getFavWeather(): Flow<List<RoomFavPojo>> {
       return fakeLocalSource.getFavWeather()
    }

    override suspend fun insertFavWeather(favWeather: RoomFavPojo) {
       fakeLocalSource.insertFavWeather(favWeather)
    }

    override suspend fun deleteFavWeather(favWeather: RoomFavPojo) {
        fakeLocalSource.deleteFavWeather(favWeather)
    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> {
       return fakeLocalSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: RoomAlertPojo) {
        fakeLocalSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        fakeLocalSource.deleteAlert(alert)
    }
}