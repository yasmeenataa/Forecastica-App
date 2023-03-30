package com.example.forecasticaapp.homePage.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.RepositoryInterface
import com.example.forecasticaapp.network.APIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val _irpo: RepositoryInterface) : ViewModel() {
    private var oneCallResponse = MutableStateFlow<APIState>(APIState.Loading)
    val data = oneCallResponse
    private var _currentWeather: MutableStateFlow<List<OneCallResponse>> = MutableStateFlow<List<OneCallResponse>>(ArrayList())
    val currentWeather =_currentWeather

    fun getOneCallResponse(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.getOneCallResponse(lat, lon, units, lang)
                ?.catch { e -> oneCallResponse.value = APIState.Failure(e) }
                ?.collect { data -> oneCallResponse.value = APIState.Success(data) }
        }
    }

    fun insertCurrentWeather(weather: OneCallResponse?) {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.insertCurrentWeather(weather)
        }
    }

    fun getCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentWeather.value =  _irpo.getCurrentWeather()

        }
    }

    fun deleteCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.deleteCurrentWeather()
        }
    }
}