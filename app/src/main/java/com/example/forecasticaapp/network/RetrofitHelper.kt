package com.example.labmvvm.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper{
    const val BASE_URL="https://dummyjson.com/"
    val RetrofitInstance= Retrofit
        .Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}