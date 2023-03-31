package com.example.forecasticaapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.RoomHomePojo



@Database(entities = [RoomHomePojo::class,RoomFavPojo::class], version = 1)
@TypeConverters(TypeConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getCurrentWeatherDao(): CurrentWeatherDAO
    abstract fun getFavoriteWeatherDao():FavoriteWeatherDAO

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        fun getInstance(context: Context): WeatherDatabase {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "WeatherDatabase"
            )
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            return instance
        }
    }
}
