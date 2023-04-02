package com.example.forecasticaapp.favourite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.RepositoryInterface
import com.example.forecasticaapp.models.RoomHomePojo
import com.example.forecasticaapp.network.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel(private val _irpo: RepositoryInterface) : ViewModel() {
    var favoriteResponse = MutableStateFlow<ResponseState<List<RoomFavPojo>>>(ResponseState.Loading)

    init {
        getFavWeather()
    }

    fun getFavWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.getFavWeather()
                ?.catch { e->favoriteResponse.value=ResponseState.Failure(e) }
                ?.collect{data->favoriteResponse.value=ResponseState.Success(data)}

        }
    }

    fun deleteFavWeather(favWeather:RoomFavPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.deleteFavWeather(favWeather)
        }
    }

}