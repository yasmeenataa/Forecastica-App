package com.example.forecasticaapp.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.forecasticaapp.MainRule
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
@MediumTest
class LocalSourceTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var weatherDatabase: WeatherDatabase
    lateinit var localDataSource: ConcreteLocalSource
    var context:Context=ApplicationProvider.getApplicationContext()
    @Before
    fun setUp() {


        weatherDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        localDataSource = ConcreteLocalSource(context)
    }

    @After
    fun closeDataBase() = weatherDatabase.close()

    @Test
    fun getAllAlerts_returnSizeOfAlerts() =mainRule.runBlockingTest{
        //Given
        val alert1 = RoomAlertPojo(1,1680638400,1680638400,36000000,"fayoum","")
        val alert2 = RoomAlertPojo(2,1680638400,1680638400,36000000,"Alex","")
        val alert3 = RoomAlertPojo(3,1680638400,1680638400,36000000,"cairo","")


        localDataSource.insertAlert(alert1)
        localDataSource.insertAlert(alert2)
        localDataSource.insertAlert(alert3)

        //When
        val result = localDataSource.getAllAlerts().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(3))
    }
    @Test
    fun insertAlert_notNull() =mainRule.runBlockingTest{
        //Given
        val alert1 = RoomAlertPojo(1,1680638400,1680638400,36000000,"fayoum","")

        localDataSource.insertAlert(alert1)


        //When
        val result = localDataSource.getAllAlerts().first()

        //Then
        Assert.assertThat(result, CoreMatchers.notNullValue())
    }
    @Test
    fun deleteAlert_returnSizeOfAlerts() = runBlockingTest{
        //Given
        val alert1 = RoomAlertPojo(1,1680638400,1680638400,36000000,"fayoum","")
        val alert2 = RoomAlertPojo(2,1680638400,1680638400,36000000,"Alex","")
        val alert3 = RoomAlertPojo(3,1680638400,1680638400,36000000,"cairo","")


        localDataSource.insertAlert(alert1)
        localDataSource.insertAlert(alert2)
        localDataSource.insertAlert(alert3)

        //When
        val result = localDataSource.getAllAlerts().first()
        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(4))
        //when
        localDataSource.deleteAlert(alert1)
        val result2 = localDataSource.getAllAlerts().first()
        //Then
        MatcherAssert.assertThat(result2.size, CoreMatchers.`is`(3))
    }

    @Test
    fun getFavWeather_returnSizeOfFav() = runBlockingTest {
        //Given
        val fav1 = RoomFavPojo(1, 48.4734, 7.9498, "Cairo")
        val fav2 = RoomFavPojo(2, 48.4734, 7.9498, "Alex")
        val fav3 = RoomFavPojo(3, 48.4734, 7.9498, "Egypt")

        localDataSource.insertFavWeather(fav1)
        localDataSource.insertFavWeather(fav2)
        localDataSource.insertFavWeather(fav3)

        //When
        val result = localDataSource.getFavWeather().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(3))
    }

    @Test
    fun insertFavoriteWeather_notNull() = runBlockingTest {
        //Given
        val fav1 = RoomFavPojo(1, 48.4734, 7.9498, "Cairo")

        localDataSource.insertFavWeather(fav1)


        //When
        val result = localDataSource.getFavWeather().first()

        //Then
        Assert.assertThat(result, CoreMatchers.notNullValue())
    }

    @Test
    fun deleteFavorite_returnSizeOfFavorites() = runBlockingTest {
        //Given
        val fav1 = RoomFavPojo(1, 48.4734, 7.9498, "Cairo")
        val fav2 = RoomFavPojo(2, 48.4734, 7.9498, "Alex")
        val fav3 = RoomFavPojo(3, 48.4734, 7.9498, "Egypt")

        localDataSource.insertFavWeather(fav1)
        localDataSource.insertFavWeather(fav2)
        localDataSource.insertFavWeather(fav3)

        //When
        val result = localDataSource.getFavWeather().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(3)) //when
        localDataSource.deleteFavWeather(fav1)
        val result2 = localDataSource.getFavWeather().first()
        //Then
        MatcherAssert.assertThat(result2.size, CoreMatchers.`is`(2))
    }
    @Test
    fun getInsertedWeather_returnCurrentWeather() = runBlockingTest {
        localDataSource.deleteCurrentWeather()
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


        localDataSource.insertCurrentWeather(weather)

        //When
        val result = localDataSource.getCurrentWeather().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(1))

    }

    @Test
    fun insertWeather_notNull() = runBlockingTest {
        localDataSource.deleteCurrentWeather()
        //Given
        localDataSource.deleteCurrentWeather()
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
        localDataSource.insertCurrentWeather(weather)

        //When
        val result = localDataSource.getCurrentWeather().first()

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
        localDataSource.insertCurrentWeather(weather)

        //When
        val result = localDataSource.getCurrentWeather().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(1))
        //When
        localDataSource.deleteCurrentWeather()
        val result2 = localDataSource.getCurrentWeather().first()

        //Then
        MatcherAssert.assertThat(result2.size, CoreMatchers.`is`(0))
    }

}