package com.example.forecasticaapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteWeather")
data class RoomFavPojo (
    @PrimaryKey(autoGenerate = true)
    var favID:Int=0,
    val lat: Double,
    val lon: Double,
    val address:String
    )