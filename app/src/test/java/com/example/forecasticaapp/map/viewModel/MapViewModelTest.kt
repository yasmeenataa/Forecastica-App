package com.example.forecasticaapp.map.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.forecasticaapp.MainRule
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.favourite.viewModel.FavoriteViewModel
import com.example.forecasticaapp.models.*
import com.example.forecasticaapp.network.RemoteSource
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
class MapViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    val fav1 = RoomFavPojo(1, 48.4734, 7.9498, "Cairo")
    val fav2 = RoomFavPojo(2, 48.4734, 7.9498, "Alex")
    val fav3 = RoomFavPojo(3, 48.4734, 7.9498, "Egypt")

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
    private val local = listOf(fav1, fav2)
    private val remote = weather1

    lateinit var repo: FakeRepository
    lateinit var mapViewModel: MapViewModel
    lateinit var favViewModel:FavoriteViewModel
    private lateinit var remoteDataSource: FakeRemoteSource
    private lateinit var localDataSource: FakeLocalSource
    @Before
    fun setUp() {
        remoteDataSource = FakeRemoteSource(remote)
        localDataSource = FakeLocalSource(local as MutableList<OneCallResponse>)
        repo = FakeRepository(remoteDataSource,localDataSource)
        mapViewModel= MapViewModel(repo)
        favViewModel= FavoriteViewModel(repo)

    }

    @Test
    fun insertFavWeather_favWeather_sizeOfList() = runBlockingTest{
        mapViewModel.insertFavWeather(fav3)
        var data:List<RoomFavPojo> = emptyList()
        favViewModel.getFavWeather()
        val result = favViewModel.favoriteResponse.first()
        when (result) {
            is ResponseState.Loading -> {

            }
            is ResponseState.Success -> {

                data = result.data
            }
            is ResponseState.Failure -> {

            }
        }

        //Then
        MatcherAssert.assertThat(data.size, CoreMatchers.`is`(1))
    }
}