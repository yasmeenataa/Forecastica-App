package com.example.forecasticaapp.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.forecasticaapp.models.Current
import com.example.forecasticaapp.models.OneCallResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CurrentWeatherDAOTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var weatherDatabase: WeatherDatabase
    private lateinit var currentWeatherDAO: CurrentWeatherDAO

    @Before
    fun setUp() {


        weatherDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        currentWeatherDAO = weatherDatabase.getCurrentWeatherDao()
    }

    @After
    fun closeDataBase() = weatherDatabase.close()

    @Test
    fun getInsertedWeather_returnCurrentWeather() = runBlockingTest {
        //Given
        val weather = OneCallResponse(
            1,
            22.2,
            hourly = emptyList(),
            daily = emptyList(),
            alerts = emptyList(),
            timezone = "cairo",
            current = Current(
                1, 22, 55, 50.0, 50.0, 12, 3, 5.0, 5.0, 5, 0, 20.0, 5,
                emptyList()
            ),
            lon=555.2,
            timezone_offset = 555
        )


        currentWeatherDAO.insertCurrentWeather(weather)

        //When
        val result = currentWeatherDAO.getCurrentWeather().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(1))
    }

    @Test
    fun insertWeather_notNull() = runBlockingTest {
        //Given
        val weather = OneCallResponse(
            1,
            22.2,
            hourly = emptyList(),
            daily = emptyList(),
            alerts = emptyList(),
            timezone = "cairo",
            current = Current(
                1, 22, 55, 50.0, 50.0, 12, 3, 5.0, 5.0, 5, 0, 20.0, 5,
                emptyList()
            ),
            lon=555.2,
            timezone_offset = 555
        )
        currentWeatherDAO.insertCurrentWeather(weather)

        //When
        val result = currentWeatherDAO.getCurrentWeather().first()

        //Then
        Assert.assertThat(result, CoreMatchers.notNullValue())
    }

    @Test
    fun deleteCurrentWeather_returnNull() = runBlockingTest {
        //Given
        val weather = OneCallResponse(
            1,
            22.2,
            hourly = emptyList(),
            daily = emptyList(),
            alerts = emptyList(),
            timezone = "cairo",
            current = Current(
                1, 22, 55, 50.0, 50.0, 12, 3, 5.0, 5.0, 5, 0, 20.0, 5,
                emptyList()
            ),
            lon=555.2,
            timezone_offset = 555
        )
        currentWeatherDAO.insertCurrentWeather(weather)

        //When
        val result = currentWeatherDAO.getCurrentWeather().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(1))
        //When
        currentWeatherDAO.deleteCurrentWeather()
        val result2 = currentWeatherDAO.getCurrentWeather().first()

        //Then
        MatcherAssert.assertThat(result2.size, CoreMatchers.`is`(0))
    }

}