package com.example.forecasticaapp.alerts.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forecasticaapp.models.RepositoryInterface

class AlertViewModelFactory (private val _irpo: RepositoryInterface): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java))
        {
            AlertViewModel(_irpo) as T
        }else{
            throw IllegalAccessException("View Model Class not found")
        }
    }
}