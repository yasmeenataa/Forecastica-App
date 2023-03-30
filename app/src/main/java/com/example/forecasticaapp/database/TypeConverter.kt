package com.example.forecasticaapp.database

import androidx.room.TypeConverter
import com.example.forecasticaapp.models.Alert
import com.example.forecasticaapp.models.Current
import com.example.forecasticaapp.models.Daily
import com.example.forecasticaapp.models.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {
    @TypeConverter
    fun fromCurrentToString(current: Current) = Gson().toJson(current)

    @TypeConverter
    fun fromStringToCurrent(stringCurrent: String) =
        Gson().fromJson(stringCurrent, Current::class.java)

    @TypeConverter
    fun fromDailyListToString(daily: List<Daily>) = Gson().toJson(daily)

    @TypeConverter
    fun fromStringToDailyList(stringDaily: String) =
        Gson().fromJson(stringDaily, Array<Daily>::class.java).toList()

    @TypeConverter
    fun fromHourlyListToString(hourly: List<Hourly>) = Gson().toJson(hourly)

    @TypeConverter
    fun fromStringToHourlyList(stringHourly: String) =
        Gson().fromJson(stringHourly, Array<Hourly>::class.java).toList()

    @TypeConverter
    fun fromAlertsToString(alerts: List<Alert>?): String {
        if (!alerts.isNullOrEmpty()) {
            return Gson().toJson(alerts)
        }
        return ""
    }

    @TypeConverter
    fun fromStringToAlerts(alerts: String?): List<Alert> {
        if (alerts.isNullOrEmpty()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<Alert?>?>() {}.type
        return Gson().fromJson(alerts, listType)
    }

}