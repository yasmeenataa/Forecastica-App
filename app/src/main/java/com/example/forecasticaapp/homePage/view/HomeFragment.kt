package com.example.forecasticaapp.homePage.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
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
import com.example.forecasticaapp.databinding.FragmentHomeBinding
import com.example.forecasticaapp.homePage.viewModel.HomeViewModel
import com.example.forecasticaapp.homePage.viewModel.HomeViewModelFactory
import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.Repository
import com.example.forecasticaapp.network.APIState
import com.example.forecasticaapp.network.ApiClient
import com.example.forecasticaapp.utils.*
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val PERMISSION_ID = 44

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var homeViewModel: HomeViewModel
    lateinit var homeViewModelFactory: HomeViewModelFactory
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var language: String
    private lateinit var units: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        (context as AppCompatActivity).supportActionBar?.title = "Home"

        homeViewModelFactory = HomeViewModelFactory(Repository.getInstance(ApiClient.getInstance()))
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        daysAdapter = DaysAdapter(ArrayList(), requireContext())
        binding.recyclerViewForday.adapter = daysAdapter
        hoursAdapter = HoursAdapter(ArrayList(), requireContext())
        binding.recyclerViewForTime.adapter = hoursAdapter
        if (isConnected(requireContext())) {
        val location=sharedPreferences.getString(Constants.LOCATION,Constants.ENUM_LOCATION.Gps.toString())
            if (location.equals(Constants.ENUM_LOCATION.Gps.toString())) {
                getLastLocation()
            }
        }
        else{
            Snackbar.make(binding.root,"You're offline, Check Internet Connection",Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions())
            getLastLocation()
    }

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
        requireActivity().recreate()
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

    fun isLocationEnabled(): Boolean {
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
                Snackbar.make(binding.root,"lat : ${latitude},lon : ${longitude}",Snackbar.LENGTH_LONG).show()
                sharedPreferences.edit().putString(Constants.GPS_LON, longitude.toString()).apply()
                sharedPreferences.edit().putString(Constants.GPS_LAT, latitude.toString()).apply()
            } else {
                latitude = sharedPreferences.getFloat(Constants.MAP_LAT, 0f).toDouble()
                longitude = sharedPreferences.getFloat(Constants.MAP_LON, 0f).toDouble()
            }
            Snackbar.make(binding.root,"lat : ${latitude},lon : ${longitude}",Snackbar.LENGTH_LONG).show()
            homeViewModel.getOneCallResponse(latitude, longitude, Constants.ENUM_UNITS.standard.toString(), "en")
            lifecycleScope.launch() {
                homeViewModel.data.collectLatest { result ->
                    when (result) {
                        is APIState.Loading -> {
                            binding.homeProgressBar.visibility = VISIBLE
                            binding.homeLinear.visibility = GONE
                        }
                        is APIState.Success -> {
                            bindingData(result.data)
                        }
                        is APIState.Failure -> {
                            binding.homeProgressBar.visibility = VISIBLE
                            binding.homeLinear.visibility = GONE
                            Snackbar.make(binding.root, result.msg.toString(), Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    @SuppressLint("MissingPermission", "VisibleForTests")
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
        binding.txtCountryNameHome.text = response.timezone
        binding.txtDateHome.text = getDayFormat(response.current.dt, language)
        binding.iconHome.setImageResource(getImageIcon(response.current.weather[0].icon))
        binding.txtDescriptionHome.text = response.current.weather[0].description
        binding.txtDegreeHome.text = response.current.temp.toString()
        binding.pressureMeasure.text =
            "${response.current.pressure} ${getString(com.example.forecasticaapp.R.string.pascal)}"
        binding.humidityMeasure.text = response.current.humidity.toString() + " %"
        binding.windMeasure.text =
            "${response.current.wind_speed} ${getSpeedUnit(requireContext())}"
        binding.visibilityMeasure.text = response.current.visibility.toString()
        binding.cloudMeasure.text = response.current.clouds.toString()
        binding.ultraVioMeasure.text = response.current.uvi.toString()

        daysAdapter.setList(response.daily)
        hoursAdapter.setList(response.hourly)
    }

}