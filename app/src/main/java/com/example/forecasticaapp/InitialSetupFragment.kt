package com.example.forecasticaapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.example.forecasticaapp.databinding.FragmentHomeBinding
import com.example.forecasticaapp.databinding.FragmentInitialSetupBinding


class InitialSetupFragment : Fragment() {
    lateinit var binding: FragmentInitialSetupBinding
    lateinit var radioButton: RadioButton
    lateinit var sharedPreference:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
             ?.getSharedPreferences("SetupSharedPreferences", Context.MODE_PRIVATE)!!
        if (sharedPreference!!.getBoolean("first_install", true)) {
            sharedPreference.edit().putBoolean("first_install", false).apply()

            binding.btnSetupSubmit.setOnClickListener {
                radioButton =
                    view.findViewById(binding.locationRadioGroupSetup.checkedRadioButtonId)
                when (radioButton.text.toString()) {
                    getString(R.string.map) -> {
                        sharedPreference.edit().putString(
                            Constants.LOCATION,
                            Constants.ENUM_LOCATION.Map.toString()
                        ).apply()
                    }
                    getString(R.string.gps) -> {
                        sharedPreference.edit().putString(
                            Constants.LOCATION,
                            Constants.ENUM_LOCATION.Gps.toString()
                        ).apply()
                    }

                }
                if(binding.notificationSwitchSetup.isChecked)
                {
                    sharedPreference.edit().putString(
                        Constants.NOTIFICATIONS,
                        Constants.ENUM_NOTIFICATIONS.Enabled.toString()
                    ).apply()
                }
                else{
                    sharedPreference.edit().putString(
                        Constants.NOTIFICATIONS,
                        Constants.ENUM_NOTIFICATIONS.Disabled.toString()
                    ).apply()
                }
                Navigation.findNavController(view)
                    .navigate(R.id.action_initialSetupFragment_to_homeFragment2)


            }
        }
        else{
            Navigation.findNavController(view)
                .navigate(R.id.action_initialSetupFragment_to_homeFragment2)

        }
    }


}