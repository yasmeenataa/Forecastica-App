package com.example.forecasticaapp.network

import com.example.forecasticaapp.models.OneCallResponse
import com.example.labmvvm.network.RetrofitHelper

class ApiClient private constructor() : RemoteSource {
    val apiService: ApiService by lazy {
        RetrofitHelper.RetrofitInstance.create(ApiService::class.java)
    }

    override suspend fun getOneCallResponse(
        lat: Double?,
        lon: Double?,
        units: String?,
        lang: String?
    ): OneCallResponse {
        return apiService.oneCallResponse(lat, lon, units, lang)
    }

    companion object {
        private var instance: ApiClient? = null
        fun getInstance(): ApiClient {
            return instance ?: synchronized(this) {
                val temp = ApiClient()
                instance = temp
                temp
            }
        }
    }
}