package com.example.forecasticaapp.favourite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.forecasticaapp.InitialSetupFragmentDirections
import com.example.forecasticaapp.R
import com.example.forecasticaapp.database.ConcreteLocalSource
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.databinding.FragmentFavoriteBinding
import com.example.forecasticaapp.favourite.viewModel.FavoriteViewModel
import com.example.forecasticaapp.favourite.viewModel.FavoriteViewModelFactory
import com.example.forecasticaapp.models.Repository
import com.example.forecasticaapp.models.RoomHomePojo
import com.example.forecasticaapp.network.ApiClient
import com.example.forecasticaapp.network.ResponseState
import com.example.forecasticaapp.utils.isConnected
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment(), OnFavoriteListener {
    lateinit var binding: FragmentFavoriteBinding
    lateinit var favouriteAdapter: FavouriteAdapter
    lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.favorite)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        favouriteAdapter = FavouriteAdapter(ArrayList(), requireContext(), this)
        favoriteViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                ApiClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        favoriteViewModel = ViewModelProvider(
            this, favoriteViewModelFactory
        )[FavoriteViewModel::class.java]
        binding.recyclerViewFav.adapter=favouriteAdapter

        lifecycleScope.launch() {
            favoriteViewModel.favoriteResponse.collectLatest { result ->
                when (result) {
                    is ResponseState.Loading -> {
                        binding.favProgressBar.visibility = View.VISIBLE
                        binding.favLottiAnimation.visibility = View.GONE
                        binding.txtNoPlaces.visibility=View.GONE
                        binding.recyclerViewFav.visibility=View.GONE
                    }
                    is ResponseState.Success -> {
                        if(result.data.isNotEmpty())
                        {
                            binding.favProgressBar.visibility = View.GONE
                            binding.favLottiAnimation.visibility = View.GONE
                            binding.txtNoPlaces.visibility=View.GONE
                            binding.recyclerViewFav.visibility=View.VISIBLE
                            favouriteAdapter.setList(result.data)
                            favouriteAdapter.notifyDataSetChanged()
                        }
                        else{
                            binding.favProgressBar.visibility = View.GONE
                            binding.favLottiAnimation.visibility = View.VISIBLE
                            binding.txtNoPlaces.visibility=View.VISIBLE
                            binding.recyclerViewFav.visibility=View.GONE
                        }
                    }
                    is ResponseState.Failure -> {
                        binding.favProgressBar.visibility = View.VISIBLE
                        binding.favLottiAnimation.visibility = View.GONE
                        binding.txtNoPlaces.visibility=View.GONE
                        binding.recyclerViewFav.visibility=View.GONE
                        Snackbar.make(binding.root, result.msg.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
        binding.addFavFloating.setOnClickListener {
            if (isConnected(requireContext())) {
                val action =
                    FavoriteFragmentDirections.actionFavoriteFragmentToMapFragment2("Favourite")
                Navigation.findNavController(requireView()).navigate(action)
            } else {
                Snackbar.make(
                    binding.root,
                    "You're offline, Check Internet Connection",
                    Snackbar.ANIMATION_MODE_FADE
                ).show()
            }


        }
    }

    override fun cardClick(favObject: RoomFavPojo) {
        val action =
            FavoriteFragmentDirections.actionFavoriteFragmentToFavouriteViewFragment(
               favObject
            )
        Navigation.findNavController(requireView()).navigate(action)

    }

    override fun deleteClick(favObject: RoomFavPojo) {
        favoriteViewModel.deleteFavWeather(favObject)
        favouriteAdapter.notifyDataSetChanged()
    }
}