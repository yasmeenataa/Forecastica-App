package com.example.forecasticaapp.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.models.RepositoryInterface
import com.example.forecasticaapp.network.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val _irpo: RepositoryInterface) : ViewModel() {

    fun insertFavWeather(favWeather: RoomFavPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.insertFavWeather(favWeather)
        }
    }
}