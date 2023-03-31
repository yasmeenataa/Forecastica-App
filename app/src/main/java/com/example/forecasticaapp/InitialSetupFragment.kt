package com.example.forecasticaapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.forecasticaapp.databinding.FragmentInitialSetupBinding
import com.example.forecasticaapp.favourite.view.FavoriteFragmentDirections
import com.example.forecasticaapp.utils.Constants
import com.example.forecasticaapp.utils.isConnected
import com.google.android.material.snackbar.Snackbar


class InitialSetupFragment : Fragment() {
    private lateinit var binding: FragmentInitialSetupBinding
    private lateinit var radioButton: RadioButton
    private lateinit var sharedPreference: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInitialSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        sharedPreference = (activity as AppCompatActivity?)
            ?.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        if (sharedPreference.getBoolean("first_install", true)) {

            binding.btnSetupSubmit.setOnClickListener {


                if (isConnected(requireContext())) {
                    sharedPreference.edit().putBoolean("first_install", false).apply()
                    if (binding.notificationSwitchSetup.isChecked) {
                        sharedPreference.edit().putString(
                            Constants.NOTIFICATIONS,
                            Constants.ENUM_NOTIFICATIONS.Enabled.toString()
                        ).apply()
                    } else {
                        sharedPreference.edit().putString(
                            Constants.NOTIFICATIONS,
                            Constants.ENUM_NOTIFICATIONS.Disabled.toString()
                        ).apply()
                    }
                    radioButton =
                        view.findViewById(binding.locationRadioGroupSetup.checkedRadioButtonId)
                    when (radioButton.text.toString()) {
                        getString(R.string.map) -> {
                            sharedPreference.edit().putString(
                                Constants.LOCATION,
                                Constants.ENUM_LOCATION.Map.toString()
                            ).apply()
                            val action =
                                InitialSetupFragmentDirections.actionInitialSetupFragmentToMapFragment2(
                                    "Home"
                                )
                            Navigation.findNavController(requireView()).navigate(action)
                        }
                        getString(R.string.gps) -> {
                            sharedPreference.edit().putString(
                                Constants.LOCATION,
                                Constants.ENUM_LOCATION.Gps.toString()
                            ).apply()
                            val action =
                                InitialSetupFragmentDirections.actionInitialSetupFragmentToHomeFragment2()
                            Navigation.findNavController(requireView()).navigate(action)
                        }
                    }

                }
                else
                {
                    Snackbar.make(binding.root,"You're Offline,Check Internet Connection",Snackbar.ANIMATION_MODE_SLIDE).show()
                }
            }
        } else {
            val action =
                InitialSetupFragmentDirections.actionInitialSetupFragmentToHomeFragment2()
            Navigation.findNavController(requireView()).navigate(action)
        }
    }
}