package com.example.forecasticaapp.alerts.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.forecasticaapp.MainRule
import com.example.forecasticaapp.database.RoomAlertPojo
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.favourite.viewModel.FavoriteViewModel
import com.example.forecasticaapp.map.viewModel.MapViewModel
import com.example.forecasticaapp.models.*
import com.example.forecasticaapp.network.ResponseState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AlertViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    val weather1 = OneCallResponse(
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
    private val remote = weather1

    lateinit var repo: FakeRepository
    lateinit var alertsViewModel: AlertViewModel
    private lateinit var remoteDataSource: FakeRemoteSource
    private lateinit var localDataSource: FakeLocalSource
    val alert1 = RoomAlertPojo(1, 1680638400, 1680638400, 36000000, "fayoum", "")
    val alert2 = RoomAlertPojo(2, 1680638400, 1680638400, 36000000, "Alex", "")
    val alert3 = RoomAlertPojo(3, 1680638400, 1680638400, 36000000, "cairo", "")


    @Before
    fun setUp() {
        remoteDataSource = FakeRemoteSource(remote)
        localDataSource = FakeLocalSource(mutableListOf())
        repo = FakeRepository(remoteDataSource, localDataSource)
        alertsViewModel = AlertViewModel(repo)

    }

    @Test
    fun getAllAlerts_returnZero() = runBlockingTest {
        alertsViewModel.getAllAlerts()
        var data: List<RoomAlertPojo> = emptyList()
        val result = alertsViewModel.alertResponse.first()

        when (result) {
            is ResponseState.Success -> {

                data = result.data
            }
            else -> {}
        }
        //Then
        MatcherAssert.assertThat(data.size, CoreMatchers.`is`(0))

    }

    @Test
    fun insertAlert_insert3Alerts_returnSize3()= runBlockingTest {
        val alert1 = RoomAlertPojo(1, 1680638400, 1680638400, 36000000, "fayoum", "")
        val alert2 = RoomAlertPojo(2, 1680638400, 1680638400, 36000000, "Alex", "")
        val alert3 = RoomAlertPojo(3, 1680638400, 1680638400, 36000000, "cairo", "")
        alertsViewModel.insertAlert(alert1)
        alertsViewModel.insertAlert(alert2)
        alertsViewModel.insertAlert(alert3)
        alertsViewModel.getAllAlerts()
        var data: List<RoomAlertPojo> = emptyList()
        val result = alertsViewModel.alertResponse.first()
        when (result) {
            is ResponseState.Success -> {

                data = result.data
            }
            else -> {}
        }
        //Then
        MatcherAssert.assertThat(data.size, CoreMatchers.`is`(3))
    }

    @Test
    fun deleteAlert_add2AlertsAndDelete1_returnOneAlert()= runBlockingTest {
          alertsViewModel.insertAlert(alert1)
        alertsViewModel.insertAlert(alert2)
        //when
        alertsViewModel.deleteAlert(alert1)
        alertsViewModel.getAllAlerts()
        var data: List<RoomAlertPojo> = emptyList()
        val result = alertsViewModel.alertResponse.first()
        when (result) {
            is ResponseState.Success -> {

                data = result.data
            }
            else -> {}
        }
        //Then
        assertThat(data.size, CoreMatchers.`is`(1))
    }
}