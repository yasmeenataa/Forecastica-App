package com.example.forecasticaapp.models

import com.example.forecasticaapp.network.RemoteSource

class FakeRemoteSource(private var oneCallResponse: OneCallResponse):RemoteSource {
    override suspend fun getOneCallResponse(
        lat: Double?,
        lon: Double?,
        units: String?,
        lang: String?
    ): OneCallResponse {
        return oneCallResponse
    }
}