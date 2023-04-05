package com.example.forecasticaapp.models

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.example.forecasticaapp.MainRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RepositoryTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    var weather1 = OneCallResponse(
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
        lon = 555.2,
        timezone_offset = 555
    )
    var weather2 = OneCallResponse(
        1,
        22.2,
        hourly = emptyList(),
        daily = emptyList(),
        alerts = emptyList(),
        timezone = "cairo",
        current = Current(
            2, 22, 55, 50.0, 50.0, 12, 3, 5.0, 5.0, 5, 0, 20.0, 5,
            emptyList()
        ),
        lon = 555.2,
        timezone_offset = 555
    )
    var weather3 = OneCallResponse(
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
        lon = 555.2,
        timezone_offset = 555
    )
    val weather4 = OneCallResponse(
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
        lon = 555.2,
        timezone_offset = 555
    )
    private val local = listOf(weather1, weather2, weather3)
    private val remote = weather4
    private val newTasks = listOf(weather4)
    private lateinit var remoteDataSource: FakeRemoteSource
    private lateinit var localDataSource: FakeLocalSource

    lateinit var repo: Repository


    @Before
    fun setUp() {
        remoteDataSource = FakeRemoteSource(remote)
        localDataSource = FakeLocalSource(local as MutableList<OneCallResponse>)
        repo = Repository.getInstance(remoteDataSource, localDataSource)
    }

    @Test
    fun getWeather_returnRemoteWeather() = mainRule.runBlockingTest {
        //when
        repo.getOneCallResponse(22.2, 555.2, "", "en").collect {
            val oneCallResponse = it
            // Then
            Assert.assertThat(oneCallResponse, IsEqual(remote))

        }
    }

    @Test
    fun getWeather_returnLocalWeather() = mainRule.runBlockingTest {
        //when
        repo.getCurrentWeather().collect {
            val oneCallResponse = it
            // Then
            Assert.assertThat(oneCallResponse, IsEqual(local))

        }
    }

}