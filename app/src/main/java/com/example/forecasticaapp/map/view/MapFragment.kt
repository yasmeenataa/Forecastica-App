package com.example.forecasticaapp.map.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.forecasticaapp.R
import com.example.forecasticaapp.database.ConcreteLocalSource
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.databinding.FragmentMapBinding
import com.example.forecasticaapp.map.viewModel.MapViewModel
import com.example.forecasticaapp.map.viewModel.MapViewModelFactory
import com.example.forecasticaapp.models.Repository
import com.example.forecasticaapp.network.ApiClient
import com.example.forecasticaapp.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.util.*


class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var geoCoder: Geocoder
    private var address: String = ""
    private lateinit var destination: String
    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapViewModelFactory: MapViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assert(arguments != null)
        destination = arguments?.let { MapFragmentArgs.fromBundle(it).destination }.toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        mapViewModelFactory = MapViewModelFactory(
            Repository.getInstance(
                ApiClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        mapViewModel = ViewModelProvider(this, mapViewModelFactory)[MapViewModel::class.java]
        sharedPreferences = (activity as AppCompatActivity?)
            ?.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        geoCoder = Geocoder(requireContext(), Locale.getDefault())

        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync { googleMap ->

            googleMap.setOnMapClickListener {
                val marker = MarkerOptions()
                marker.position(it)
                googleMap.clear()
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 5f))
                googleMap.addMarker(marker)
                val lat = it.latitude
                val lon = it.longitude
                val addresses = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                val city = addresses!![0].locality
                val country = addresses[0].countryName
                address = "$country/$city"
                when (destination) {
                    "Home" -> {

                        binding.btnSaveLocation.setOnClickListener {
                            val alertDialog = AlertDialog.Builder(context)

                            alertDialog.apply {
                                setIcon(R.drawable.add_location)
                                setTitle(getString(R.string.add_location))
                                setMessage("Are you sure you want to add ${address} to favorite?")
                                setPositiveButton(getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                                    sharedPreferences.edit().putFloat(Constants.MAP_LONH, lat.toFloat())
                                        .apply()
                                    sharedPreferences.edit()
                                        .putFloat(Constants.MAP_LATH, lon.toFloat()).apply()
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_mapFragment2_to_homeFragment2)
                                }
                                setNegativeButton(getString(R.string.cancel)) { _, _ ->
                                }.create().show()
                            }
                        }


                    }
                    "Favourite" -> {
                        binding.btnSaveLocation.setOnClickListener {
                            if (city == null) {
                                Snackbar.make(
                                    binding.root,
                                    "Sorry, Couldn't find city",
                                    Snackbar.LENGTH_LONG
                                )
                                    .show()
                            } else {
                                val alertDialog = AlertDialog.Builder(context)

                                alertDialog.apply {
                                    setIcon(R.drawable.add_location)
                                    setTitle(getString(R.string.add_location))
                                    setMessage("Are you sure you want to add ${address} to favorite?")
                                    setPositiveButton(getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                                        val roomFavPojo =
                                            RoomFavPojo(lat = lat, lon = lon, address = address)

                                        mapViewModel.insertFavWeather(roomFavPojo)

                                        Navigation.findNavController(view)
                                            .navigate(R.id.action_mapFragment2_to_favoriteFragment)
                                    }
                                    setNegativeButton(getString(R.string.cancel)) { _, _ ->
                                    }.create().show()
                                }
                            }
                        }
                    }

                }
            }

        }
        binding.btnSaveLocation.setOnClickListener {

            if (address == "") {

                Snackbar.make(
                    binding.root,
                    "First, You have to mark the location",
                    Snackbar.LENGTH_LONG
                )
                    .show()

            }
        }
    }

}

