package com.example.forecasticaapp.models

import com.example.forecasticaapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository private constructor(
    var remoteSource: RemoteSource

):RepositoryInterface {
    override suspend fun getOneCallResponse(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<OneCallResponse> {
       return flowOf(remoteSource.getOneCallResponse(lat,lon,units,lang))
    }

    companion object{
        private var instance: Repository?=null
        fun getInstance(remoteSource: RemoteSource): Repository {
            return instance?: synchronized(this){
                val temp= Repository(remoteSource )
                instance=temp
                temp
            }
        }
    }
}