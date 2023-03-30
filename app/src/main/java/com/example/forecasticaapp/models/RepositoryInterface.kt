package com.example.forecasticaapp.models

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getOneCallResponse(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<OneCallResponse>
}