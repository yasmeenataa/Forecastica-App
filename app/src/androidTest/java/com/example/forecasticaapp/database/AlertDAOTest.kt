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
class AlertDAOTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var weatherDatabase: WeatherDatabase
    private lateinit var alertDao: AlertDAO

    @Before
    fun setUp() {


        weatherDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        alertDao = weatherDatabase.getAlertDao()
    }
    @After
    fun closeDataBase() = weatherDatabase.close()

    @Test
    fun getAllAlerts_returnSizeOfAlerts() = runBlockingTest{
        //Given
        val alert1 = RoomAlertPojo(1,1680638400,1680638400,36000000,"fayoum","")
        val alert2 = RoomAlertPojo(2,1680638400,1680638400,36000000,"Alex","")
        val alert3 = RoomAlertPojo(3,1680638400,1680638400,36000000,"cairo","")


        alertDao.insertAlert(alert1)
        alertDao.insertAlert(alert2)
        alertDao.insertAlert(alert3)

        //When
        val result = alertDao.getAllAlerts().first()

        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(3))
    }
    @Test
    fun insertAlert_notNull() = runBlockingTest{
        //Given
        val alert1 = RoomAlertPojo(1,1680638400,1680638400,36000000,"fayoum","")

        alertDao.insertAlert(alert1)


        //When
        val result = alertDao.getAllAlerts().first()

        //Then
        Assert.assertThat(result, CoreMatchers.notNullValue())
    }
    @Test
    fun deleteAlert_returnSizeOfAlerts() = runBlockingTest{
        //Given
        val alert1 = RoomAlertPojo(1,1680638400,1680638400,36000000,"fayoum","")
        val alert2 = RoomAlertPojo(2,1680638400,1680638400,36000000,"Alex","")
        val alert3 = RoomAlertPojo(3,1680638400,1680638400,36000000,"cairo","")


        alertDao.insertAlert(alert1)
        alertDao.insertAlert(alert2)
        alertDao.insertAlert(alert3)

        //When
        val result = alertDao.getAllAlerts().first()
        //Then
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(3))
        //when
        alertDao.deleteAlert(alert1)
        val result2 = alertDao.getAllAlerts().first()
        //Then
        MatcherAssert.assertThat(result2.size, CoreMatchers.`is`(2))
    }

}