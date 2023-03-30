package com.example.forecasticaapp.network

import com.example.forecasticaapp.models.OneCallResponse

interface RemoteSource {
    suspend fun getOneCallResponse(lat:Double,lon:Double,units:String,lang:String): OneCallResponse
}