package com.example.forecasticaapp.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.forecasticaapp.InitialSetupFragmentDirections
import com.example.forecasticaapp.R
import com.example.forecasticaapp.databinding.FragmentFavoriteBinding
import com.example.forecasticaapp.databinding.FragmentSettingsBinding
import com.example.forecasticaapp.utils.Constants
import java.util.*


class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.settings)

        binding.languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val languageRadioButton: RadioButton = view.findViewById<View>(checkedId) as RadioButton
            when (languageRadioButton.text) {
                getString(R.string.arabic) -> {
                    sharedPreferences.edit()
                        .putString(Constants.LANGUAGE, Constants.Enum_lANGUAGE.ar.toString())
                        .apply()
                    changeLanguageLocaleTo("ar")
                }
                getString(R.string.english) -> {
                    sharedPreferences.edit()
                        .putString(Constants.LANGUAGE, Constants.Enum_lANGUAGE.en.toString())
                        .apply()
                    changeLanguageLocaleTo( "en")
                }
            }

        }
        binding.btnSwitchNotify.setOnCheckedChangeListener{_,isChecked->
            if(isChecked)
            {
                sharedPreferences.edit()
                    .putString(Constants.NOTIFICATIONS, Constants.ENUM_NOTIFICATIONS.Enabled.toString())
                    .apply()
            }
            else{
                sharedPreferences.edit()
                    .putString(Constants.NOTIFICATIONS, Constants.ENUM_NOTIFICATIONS.Disabled.toString())
                    .apply()
            }

        }
        binding.locationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val locationRadioGroup: RadioButton = view.findViewById<View>(checkedId) as RadioButton
            when (locationRadioGroup.text) {
                getString(R.string.gps) -> {
                    sharedPreferences.edit()
                        .putString(Constants.LOCATION, Constants.ENUM_LOCATION.Gps.toString())
                        .apply()
                }
                getString(R.string.map) -> {
                    sharedPreferences.edit()
                        .putString(Constants.LOCATION, Constants.ENUM_LOCATION.Map.toString())
                        .apply()
                    val action =
                        SettingsFragmentDirections.actionSettingsFragment2ToMapFragment2("Home")
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }

        }
        binding.temperatureRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val temperatureRadioGroup = view.findViewById<View>(checkedId) as RadioButton
            when (temperatureRadioGroup.text.toString()) {
                getString(R.string.celsius) -> {
                    sharedPreferences.edit()
                        .putString(Constants.UNITS, Constants.ENUM_UNITS.metric.toString()).apply()
                }
                getString(R.string.fahrenheit) -> {
                    sharedPreferences.edit()
                        .putString(Constants.UNITS, Constants.ENUM_UNITS.imperial.toString()).apply()
                }
                getString(R.string.kelvin) -> {
                    sharedPreferences.edit()
                        .putString(Constants.UNITS, Constants.ENUM_UNITS.standard.toString()).apply()
                }
            }
        }

    }

    private fun changeLanguageLocaleTo(lan: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lan)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
    fun setupUI() {
        var lang = sharedPreferences.getString(Constants.LANGUAGE, Constants.Enum_lANGUAGE.en.toString())
        var LOCATION = sharedPreferences.getString(Constants.LOCATION, Constants.ENUM_LOCATION.Gps.toString())
        var notification = sharedPreferences.getString(Constants.NOTIFICATIONS, Constants.ENUM_NOTIFICATIONS.Enabled.toString())
        var units = sharedPreferences.getString(Constants.UNITS, Constants.ENUM_UNITS.standard.toString())
        if (lang == "en") {
            binding.languageRadioGroup.check(binding.englishRadioButton.id)
        }
        else
        {
            binding.languageRadioGroup.check(binding.arabicRadioButton.id)
        }

        if (LOCATION == Constants.ENUM_LOCATION.Gps.toString()) {
            binding.locationRadioGroup.check(binding.gpsRadioButton.id)
        }
       else {
            binding.locationRadioGroup.check(binding.mapRadioButton.id)
        }

        binding.btnSwitchNotify.isChecked = notification == Constants.ENUM_NOTIFICATIONS.Enabled.toString()

        if (units == Constants.ENUM_UNITS.metric.toString()) {
            binding.temperatureRadioGroup.check(binding.celsiusRadioButton.id)
        }
        if (units == Constants.ENUM_UNITS.imperial.toString()) {
            binding.temperatureRadioGroup.check(binding.fahrenheitRadioButton.id)
        }
       else {
            binding.temperatureRadioGroup.check(binding.kelvinRadioButton.id)
        }


    }
}