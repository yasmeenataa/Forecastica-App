package com.example.forecasticaapp.favourite.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.forecasticaapp.database.ConcreteLocalSource
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.databinding.FragmentFavoriteBinding
import com.example.forecasticaapp.databinding.FragmentFavouriteViewBinding
import com.example.forecasticaapp.homePage.view.DaysAdapter
import com.example.forecasticaapp.homePage.view.HoursAdapter
import com.example.forecasticaapp.homePage.viewModel.HomeViewModel
import com.example.forecasticaapp.homePage.viewModel.HomeViewModelFactory
import com.example.forecasticaapp.models.Repository
import com.example.forecasticaapp.models.RoomHomePojo
import com.example.forecasticaapp.network.ApiClient
import com.example.forecasticaapp.network.ResponseState
import com.example.forecasticaapp.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteViewFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteViewBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var language: String
    private lateinit var units: String
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavouriteViewBinding.inflate(inflater, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as AppCompatActivity).supportActionBar?.title = "Favorite"
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                ApiClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        var favpojo = arguments?.let { FavouriteViewFragmentArgs.fromBundle(it).favPojo }
        daysAdapter = DaysAdapter(ArrayList(), requireContext())
        binding.recyclerViewFordayFavView.adapter = daysAdapter
        hoursAdapter = HoursAdapter(ArrayList(), requireContext())
        binding.recyclerViewForTimeFavView.adapter = hoursAdapter
        if (isConnected(requireContext())) {


        if (favpojo != null) {
            homeViewModel.getOneCallResponse(
                favpojo.lat,
                favpojo.lon,
                units,
                language
            )
        }
        lifecycleScope.launch() {
            homeViewModel.oneCallResponse.collectLatest { result ->
                when (result) {
                    is ResponseState.Loading -> {
                        binding.favViewProgressBar.visibility = View.VISIBLE
                        binding.favViewLinear.visibility = View.GONE
                    }
                    is ResponseState.Success -> {
                        binding.favViewProgressBar.visibility = View.GONE
                        binding.favViewLinear.visibility = View.VISIBLE
                        binding.txtCountryNameFavView.text = result.data.timezone
                        binding.txtDateFavView.text = getDayFormat(result.data.current.dt, language)
                        binding.iconFavView.setImageResource(getImageIcon(result.data.current.weather[0].icon))
                        binding.txtDescriptionFavView.text = result.data.current.weather[0].description
                        binding.txtDegreeFavView.text = result.data.current.temp.toString()
                        binding.pressureMeasureFavView.text =
                            "${result.data.current.pressure} ${getString(com.example.forecasticaapp.R.string.pascal)}"
                        binding.humidityMeasureFavView.text = result.data.current.humidity.toString() + " %"
                        binding.windMeasureFavView.text =
                            "${result.data.current.wind_speed} ${getSpeedUnit(requireContext())}"
                        binding.visibilityMeasureFavView.text = result.data.current.visibility.toString()
                        binding.cloudMeasureFavView.text = result.data.current.clouds.toString()
                        binding.ultraVioMeasureFavView.text = result.data.current.uvi.toString()

                        daysAdapter.setList(result.data.daily)
                        hoursAdapter.setList(result.data.hourly)

                    }
                    is ResponseState.Failure -> {
                        binding.favViewProgressBar.visibility = View.VISIBLE
                        binding.favViewLinear.visibility = View.GONE
                        Snackbar.make(binding.root, result.msg.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

    }
        else{
            Snackbar.make(
                binding.root,
                "You're offline, Check Internet Connection",
                Snackbar.ANIMATION_MODE_FADE
            ).show()
        }
    }
}