package com.example.forecasticaapp.favourite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forecasticaapp.homePage.viewModel.HomeViewModel
import com.example.forecasticaapp.models.RepositoryInterface

class FavoriteViewModelFactory (private val _irpo: RepositoryInterface): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java))
        {
            FavoriteViewModel(_irpo) as T
        }else{
            throw IllegalAccessException("View Model Class not found")
        }
    }
}