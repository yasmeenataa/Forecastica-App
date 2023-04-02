package com.example.forecasticaapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AlertTable")
data class RoomAlertPojo(
    @PrimaryKey(autoGenerate = true)
    var alertID:Int=0,
    val dateFrom:String,
    val dateTo:String,
    val time:String,
    val countryName:String,
    val description:String
)