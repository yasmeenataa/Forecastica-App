package com.example.forecasticaapp.models

import com.example.forecasticaapp.database.LocalSource
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

    override suspend fun getCurrentWeather(): List<OneCallResponse> {
        return localSource.getCurrentWeather()
    }

    override suspend fun deleteCurrentWeather() {
        localSource.deleteCurrentWeather()
    }

    override suspend fun insertCurrentWeather(weather: OneCallResponse?) {
        localSource.insertCurrentWeather(weather)
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