package com.example.forecasticaapp.models

import com.example.forecasticaapp.database.LocalSource
import com.example.forecasticaapp.database.RoomAlertPojo
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository private constructor(
    var remoteSource: RemoteSource, var localSource: LocalSource

) : RepositoryInterface {
    override suspend fun getOneCallResponse(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<OneCallResponse> {
        return flowOf(remoteSource.getOneCallResponse(lat, lon, units, lang))
    }

    override  fun getCurrentWeather(): Flow<List<RoomHomePojo>> {
        return localSource.getCurrentWeather()
    }

    override suspend fun deleteCurrentWeather() {
        localSource.deleteCurrentWeather()
    }

    override suspend fun insertCurrentWeather(weather: RoomHomePojo?) {
        localSource.insertCurrentWeather(weather)
    }

    override fun getFavWeather(): Flow<List<RoomFavPojo>> {
       return localSource.getFavWeather()
    }

    override suspend fun insertFavWeather(favWeather: RoomFavPojo) {
        localSource.insertFavWeather(favWeather)
    }

    override suspend fun deleteFavWeather(favWeather: RoomFavPojo) {
        localSource.deleteFavWeather(favWeather)
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        localSource.deleteAlert(alert)
    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> {
       return localSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: RoomAlertPojo) {
       localSource.insertAlert(alert)
    }


    companion object {
        private var instance: Repository? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): Repository {
            return instance ?: synchronized(this) {
                val temp = Repository(remoteSource,localSource)
                instance = temp
                temp
            }
        }
    }
}