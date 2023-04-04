package com.example.forecasticaapp.alerts.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecasticaapp.database.RoomAlertPojo
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.models.RepositoryInterface
import com.example.forecasticaapp.network.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel(private val _irpo: RepositoryInterface) : ViewModel() {
    var alertResponse = MutableStateFlow<ResponseState<List<RoomAlertPojo>>>(ResponseState.Loading)

    init {
        getAllAlerts()
    }

    fun getAllAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.getAllAlerts()
                ?.catch { e->alertResponse.value= ResponseState.Failure(e) }
                ?.collect{data->alertResponse.value= ResponseState.Success(data)}

        }
    }
    fun  insertAlert(alert:RoomAlertPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.insertAlert(alert)
        }
    }
    fun deleteAlert(alert:RoomAlertPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            _irpo.deleteAlert(alert)
        }
    }

}
