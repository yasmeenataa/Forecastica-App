package com.example.forecasticaapp.homePage.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.forecasticaapp.R
import com.example.forecasticaapp.database.ConcreteLocalSource
import com.example.forecasticaapp.databinding.FragmentHomeBinding
import com.example.forecasticaapp.homePage.viewModel.HomeViewModel
import com.example.forecasticaapp.homePage.viewModel.HomeViewModelFactory
import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.Repository
import com.example.forecasticaapp.models.RoomHomePojo
import com.example.forecasticaapp.network.ApiClient
import com.example.forecasticaapp.network.ResponseState
import com.example.forecasticaapp.utils.*
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val PERMISSION_ID = 44

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var language: String
    private lateinit var units: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        language =
            sharedPreferences.getString(Constants.LANGUAGE, Constants.Enum_lANGUAGE.en.toString())
                .toString()
        units =
            sharedPreferences.getString(Constants.UNITS, Constants.ENUM_UNITS.standard.toString())
                .toString()

        return binding.root
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.home)

        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                ApiClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        daysAdapter = DaysAdapter(ArrayList(), requireContext())
        binding.recyclerViewForday.adapter = daysAdapter
        hoursAdapter = HoursAdapter(ArrayList(), requireContext())
        binding.recyclerViewForTime.adapter = hoursAdapter
        if (isConnected(requireContext())) {
            val location = sharedPreferences.getString(
                Constants.LOCATION,
                Constants.ENUM_LOCATION.Gps.toString()
            )
            if (location.equals(Constants.ENUM_LOCATION.Gps.toString())) {
                    getLastLocation()
            } else {
                latitude = sharedPreferences.getFloat(Constants.MAP_LATH, 0f).toDouble()
                longitude = sharedPreferences.getFloat(Constants.MAP_LONH, 0f).toDouble()
                callAPI(latitude, longitude, units, language)
            }
        }
        else {
            Snackbar.make(
                binding.root,
                getString(R.string.checkInternet),
                Snackbar.LENGTH_LONG
            ).show()
            homeViewModel.getCurrentWeather()
            lifecycleScope.launch {
                homeViewModel._currentWeather.collect { weather ->
                    when (weather) {
                        is ResponseState.Loading -> {
                            binding.homeProgressBar.visibility = VISIBLE
                            binding.homeLinear.visibility = GONE
                        }
                        is ResponseState.Success -> {
                            bindingData(weather.data[0])

                        }
                        is ResponseState.Failure -> {
                            binding.homeProgressBar.visibility = VISIBLE
                            binding.homeLinear.visibility = GONE
                            Snackbar.make(
                                binding.root,
                                weather.msg.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        if (checkPermissions())
//            getLastLocation()
//    }

    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()

            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()

        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            if (lastLocation != null) {
                latitude = lastLocation.latitude
                longitude = lastLocation.longitude
                sharedPreferences.edit().putString(Constants.GPS_LON, longitude.toString()).apply()
                sharedPreferences.edit().putString(Constants.GPS_LAT, latitude.toString()).apply()


            }else{
                latitude  =  sharedPreferences.getFloat(Constants.GPS_LAT, 0f).toDouble()
                longitude = sharedPreferences.getFloat(Constants.GPS_LON, 0f).toDouble()
            }

            callAPI(latitude, longitude, units, language)

            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("SetTextI18n")
    fun bindingData(response: OneCallResponse) {
        binding.homeProgressBar.visibility = GONE
        binding.homeLinear.visibility = VISIBLE
        val geocoder=Geocoder(requireContext(),Locale.getDefault())
        val countryName: String? =
            geocoder.getFromLocation(response.lat,response.lon,1)?.get(0)?.adminArea
        sharedPreferences.edit().putString(Constants.COUNTRY_NAME,countryName).apply()
        binding.txtCountryNameHome.text = countryName
        val sdf = SimpleDateFormat("dd MMM, yyyy \n hh:mm")
        val currentDate = sdf.format(Date())
        binding.txtDateHome.text = currentDate
        binding.iconHome.setImageResource(getImageIcon(response.current.weather[0].icon))
        binding.txtDescriptionHome.text = response.current.weather[0].description
        binding.txtDegreeHome.text = response.current.temp.toString()+ getTemperatureUnit(requireContext())
        binding.pressureMeasure.text =
            "${response.current.pressure} ${getString(com.example.forecasticaapp.R.string.pascal)}"
        binding.humidityMeasure.text = response.current.humidity.toString() + " %"
        binding.windMeasure.text =
            "${response.current.wind_speed} ${getSpeedUnit(requireContext())}"
        binding.visibilityMeasure.text = response.current.visibility.toString()
        binding.cloudMeasure.text = response.current.clouds.toString()
        binding.ultraVioMeasure.text = response.current.uvi.toString()

        daysAdapter.setList(response.daily.drop(1))
        hoursAdapter.setList(response.hourly)
    }

    private fun callAPI(lat: Double, lon: Double, units: String, lang: String) {

        homeViewModel.getOneCallResponse(
            lat,
            lon,
            units,
            lang
        )
        lifecycleScope.launch() {
            homeViewModel.oneCallResponse.collectLatest { result ->
                when (result) {
                    is ResponseState.Loading -> {
                        binding.homeProgressBar.visibility = VISIBLE
                        binding.homeLinear.visibility = GONE
                    }
                    is ResponseState.Success -> {
                        if(Constants.FIRST_INSTALL==1)
                        homeViewModel.insertCurrentWeather(result.data)
                        else {
                            homeViewModel.deleteCurrentWeather()
                            homeViewModel.insertCurrentWeather(result.data)
                        }
                        bindingData(result.data)
                    }
                    is ResponseState.Failure -> {
                        binding.homeProgressBar.visibility = VISIBLE
                        binding.homeLinear.visibility = GONE
                        Snackbar.make(binding.root, result.msg.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

    }

}