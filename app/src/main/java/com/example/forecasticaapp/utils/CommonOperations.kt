package com.example.forecasticaapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.forecasticaapp.R
import java.text.SimpleDateFormat
import java.util.*


fun getDayFormat(dt: Long, lang: String): String? {
    val date = Date(dt * 1000)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale(lang))
}

fun getTimeHourlyFormat(dt: Long,lang: String): String {
    val date = Date(dt * 1000)
    val format = SimpleDateFormat("h:mm a", Locale(lang))
    return format.format(date)
}

fun getSpeedUnit(context: Context): String {
    val sharedPreference =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    return when (sharedPreference.getString(
        Constants.UNITS,
        Constants.ENUM_UNITS.standard.toString()
    )) {

        Constants.ENUM_UNITS.imperial.toString() -> {
            context.getString(R.string.MilePerHour)
        }
        else -> {
            context.getString(R.string.MeterPerSecond)
        }
    }
}

fun getTemperatureUnit(context: Context): String {
    val sharedPreference =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    return when (sharedPreference.getString(
        Constants.UNITS,
        Constants.ENUM_UNITS.standard.toString()
    )) {

        Constants.ENUM_UNITS.imperial.toString() -> {
            context.getString(R.string.Fahrenheit)
        }
        Constants.ENUM_UNITS.standard.toString() -> {
            context.getString(R.string.Kelvin)
        }
        else -> {
            context.getString(R.string.Celsius)
        }
    }
}

fun getImageIcon(icon: String): Int {
    val iconValue: Int
    when (icon) {
        "01d" -> iconValue = R.drawable.ic_clear_sky_morning
        "01n" -> iconValue = R.drawable.ic_clear_sky_dawn
        "02d" -> iconValue = R.drawable.ic_few_cloud_morning
        "02n" -> iconValue = R.drawable.ic_few_cloud_night
        "03n" -> iconValue = R.drawable.ic_scattered_clouds
        "03d" -> iconValue = R.drawable.ic_scattered_clouds
        "04d" -> iconValue = R.drawable.ic_broken_cloud
        "04n" -> iconValue = R.drawable.ic_broken_cloud
        "09d" -> iconValue = R.drawable.ic_shower_raint
        "09n" -> iconValue = R.drawable.ic_shower_raint
        "10d" -> iconValue = R.drawable.ic_rain
        "10n" -> iconValue = R.drawable.ic_rain
        "11d" -> iconValue = R.drawable.ic_thunderstorm
        "11n" -> iconValue = R.drawable.ic_thunderstorm
        "13d" -> iconValue = R.drawable.ic_snow
        "13n" -> iconValue = R.drawable.ic_snow
        "50d" -> iconValue = R.drawable.ic_mist
        "50n" -> iconValue = R.drawable.ic_mist
        else -> iconValue = R.drawable.icon
    }
    return iconValue
}

fun isConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
        .isConnected
}
fun getDateToAlert(timestamp: Long, language: String): String{
    return SimpleDateFormat("dd MMM, yyyy",Locale(language)).format(timestamp)
}
fun getTimeToAlert(timestamp: Long, language: String): String{
    return SimpleDateFormat("h:mm a",Locale(language)).format(timestamp)
}
fun convertDateToLong(date:String): Long {
    val format=SimpleDateFormat("dd MMM, yyyy")
   return format.parse(date).time
}
fun convertTimeToLong(time:String):Long{
    val format = SimpleDateFormat("hh:mm a")
    return format.parse(time).time
}