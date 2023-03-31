package com.example.forecasticaapp.map

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.forecasticaapp.R
import com.example.forecasticaapp.databinding.FragmentMapBinding
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
    private lateinit var address: String
    private lateinit var destination:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assert(arguments != null)
        destination= arguments?.let { MapFragmentArgs.fromBundle(it).destination }.toString()

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
                when(destination)
                {
                    "Home"->
                    {
                        sharedPreferences.edit().putFloat(Constants.MAP_LONH, lat.toFloat())
                            .apply()
                        sharedPreferences.edit()
                            .putFloat(Constants.MAP_LATH, lon.toFloat()).apply()
                        binding.btnSaveLocation.setOnClickListener {
                            Navigation.findNavController(view)
                                .navigate(R.id.action_mapFragment2_to_homeFragment2)
                        }
                    }
                    "Favourite"->
                    {
                        sharedPreferences.edit().putString(Constants.MAP_LONF, lat.toString())
                            .apply()
                        sharedPreferences.edit()
                            .putString(Constants.MAP_LATF, lon.toString()).apply()

                        if (city != null) {
                            sharedPreferences.edit().putString(Constants.MAP_ADDRESS, address).apply()
                        } else {
                            Snackbar.make(binding.root, "Sorry, Couldn't find city", Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }
                }

            }
        }
    }
}