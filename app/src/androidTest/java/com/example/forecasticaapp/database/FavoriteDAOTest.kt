package com.example.forecasticaapp.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
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
class FavoriteDAOTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var weatherDatabase: WeatherDatabase
    private lateinit var favDAO: FavoriteWeatherDAO

    @Before
    fun setUp() {


        weatherDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        favDAO = weatherDatabase.getFavoriteWeatherDao()
    }

    @After
    fun closeDataBase() = weatherDatabase.close()

    @Test
    fun getFavWeather_returnSizeOfFav() = runBlockingTest {
        //Given
        val fav1 = RoomFavPojo(1, 48.4734, 7.9498, "Cairo")
        val fav2 = RoomFavPojo(2, 48.4734, 7.9498, "Alex")
        val fav3 = RoomFavPojo(3, 48.4734, 7.9498, "Egypt")

        favDAO.insertFavWeather(fav1)
        favDAO.insertFavWeather(fav2)
        favDAO.insertFavWeather(fav3)

        //When
        val result = favDAO.getFavWeather().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(3))
    }

    @Test
    fun insertFavoriteWeather_notNull() = runBlockingTest {
        //Given
        val fav1 = RoomFavPojo(1, 48.4734, 7.9498, "Cairo")

        favDAO.insertFavWeather(fav1)


        //When
        val result = favDAO.getFavWeather().first()

        //Then
        Assert.assertThat(result, CoreMatchers.notNullValue())
    }

    @Test
    fun deleteFavorite_returnSizeOfFavorites() = runBlockingTest {
        //Given
        val fav1 = RoomFavPojo(1, 48.4734, 7.9498, "Cairo")
        val fav2 = RoomFavPojo(2, 48.4734, 7.9498, "Alex")
        val fav3 = RoomFavPojo(3, 48.4734, 7.9498, "Egypt")

        favDAO.insertFavWeather(fav1)
        favDAO.insertFavWeather(fav2)
        favDAO.insertFavWeather(fav3)

        //When
        val result = favDAO.getFavWeather().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(3)) //when
        favDAO.deleteFavWeather(fav1)
        val result2 = favDAO.getFavWeather().first()
        //Then
        MatcherAssert.assertThat(result2.size, CoreMatchers.`is`(2))
    }

}