package com.example.forecasticaapp.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CurrentWeather")

data class RoomHomePojo(
    @PrimaryKey(autoGenerate = true)
    var id: Int=1,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Long,
    val current: Current,
    val hourly: List<Hourly>,
    val daily: List<Daily>,
    val alerts: List<Alert>?

)


