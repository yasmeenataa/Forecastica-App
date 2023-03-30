package com.example.forecasticaapp.utils

object Constants{
    const val LOCATION = "LOCATION"
    const val LANGUAGE="LANGUAGE"
    const val UNITS="units"
    enum class ENUM_LOCATION(){Map,Gps}
    const val NOTIFICATIONS = "NOTIFICATIONS"
    enum class ENUM_NOTIFICATIONS(){Enabled,Disabled}
    const val GPS_LONGITUDE="GPS_LONGITUDE"
    const val GPS_LATITUDE="GPS_LATITUDE"
    const val SHARED_PREFERENCE_NAME="SetupSharedPreferences"
    const val API_ID="40dac0af7018969cbb541943f944ba29"
    const val BASE_URL="https://api.openweathermap.org/data/3.0/"
    enum class ENUM_UNITS(){standard,metric,imperial}
    enum class Enum_lANGUAGE(){ar,en}
    enum class Enum_ALERT(){ALARM,NOTIFICATION}
    const val GPS_LON="GPS_LON"
    const val GPS_LAT="GPS_LAT"
    const val MAP_LON="MAP_LON"
    const val MAP_LAT="MAP_LAT"



}