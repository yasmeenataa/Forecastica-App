package com.example.forecasticaapp.homePage.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.RepositoryInterface
import com.example.forecasticaapp.models.RoomHomePojo

import com.example.forecasticaapp.network.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val _irpo: RepositoryInterface) : ViewModel() {
     var oneCallResponse = MutableStateFlow<ResponseState<OneCallResponse>>(ResponseState.Loading)

     var _currentWeather= MutableStateFlow<ResponseState<List<OneCallResponse>>>(ResponseState.Loading)


    fun getOneCallResponse(
        lat: Double?,
        lon: Double?,
        units: String?,
        lang: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.getOneCallResponse(lat, lon, units, lang)
                ?.catch { e -> oneCallResponse.value = ResponseState.Failure(e) }
                ?.collect { data -> oneCallResponse.value = ResponseState.Success(data) }
        }
    }

    fun insertCurrentWeather(weather: OneCallResponse?) {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.insertCurrentWeather(weather)
        }
    }

    fun getCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
             _irpo.getCurrentWeather()
                 ?.catch { e->_currentWeather.value=ResponseState.Failure(e) }
                 ?.collect{data->_currentWeather.value=ResponseState.Success(data)}

        }
    }

    fun deleteCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.deleteCurrentWeather()
        }
    }
}