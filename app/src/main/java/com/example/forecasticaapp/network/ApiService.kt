package com.example.forecasticaapp.network

import com.example.forecasticaapp.utils.Constants
import com.example.forecasticaapp.models.OneCallResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("onecall")
    suspend fun oneCallResponse(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") language: String,
        @Query("appid") appid: String = Constants.API_ID,
    ) :OneCallResponse
}
