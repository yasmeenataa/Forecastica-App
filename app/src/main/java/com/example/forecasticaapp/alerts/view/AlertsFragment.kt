package com.example.forecasticaapp.alerts.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.work.*
import com.example.forecasticaapp.MainActivity
import com.example.forecasticaapp.R
import com.example.forecasticaapp.alerts.viewModel.AlertViewModel
import com.example.forecasticaapp.alerts.viewModel.AlertViewModelFactory
import com.example.forecasticaapp.database.ConcreteLocalSource
import com.example.forecasticaapp.database.RoomAlertPojo
import com.example.forecasticaapp.databinding.FragmentAlertsBinding
import com.example.forecasticaapp.models.Repository
import com.example.forecasticaapp.network.ApiClient
import com.example.forecasticaapp.network.ResponseState
import com.example.forecasticaapp.utils.Constants
import com.example.forecasticaapp.utils.convertDateToLong
import com.example.forecasticaapp.utils.convertTimeToLong
import com.example.forecasticaapp.utils.getTimeToAlert
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AlertsFragment : Fragment(), OnAlertListener {
    private lateinit var binding: FragmentAlertsBinding
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var countryName: String
    private var dateFrom: Long = 0
    private var dateTo: Long = 0
    private var time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "CutPasteId", "NotifyDataSetChanged",
        "SuspiciousIndentation"
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as AppCompatActivity).supportActionBar?.title = "Alerts"
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()

        checkOverlayPermission()
        alertAdapter = AlertAdapter(ArrayList(), requireContext(), this)
        alertViewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                ApiClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        alertViewModel = ViewModelProvider(
            this, alertViewModelFactory
        )[AlertViewModel::class.java]
        binding.recyclerViewAlert.adapter = alertAdapter


        sharedPreference = (context)
            ?.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        countryName = sharedPreference.getString(Constants.COUNTRY_NAME, "Fayoum").toString()


        lifecycleScope.launch() {
            alertViewModel.alertResponse.collectLatest { result ->
                when (result) {
                    is ResponseState.Loading -> {
                        binding.alertProgressBar.visibility = View.VISIBLE
                        binding.alertLottiAnimation.visibility = View.GONE
                        binding.txtNoAlerts.visibility = View.GONE
                        binding.recyclerViewAlert.visibility = View.GONE
                    }
                    is ResponseState.Success -> {
                        if (result.data.isNotEmpty()) {
                            binding.alertProgressBar.visibility = View.GONE
                            binding.alertLottiAnimation.visibility = View.GONE
                            binding.txtNoAlerts.visibility = View.GONE
                            binding.recyclerViewAlert.visibility = View.VISIBLE
                            alertAdapter.setList(result.data)
                            alertAdapter.notifyDataSetChanged()
                        } else {
                            binding.alertProgressBar.visibility = View.GONE
                            binding.alertLottiAnimation.visibility = View.VISIBLE
                            binding.txtNoAlerts.visibility = View.VISIBLE
                            binding.recyclerViewAlert.visibility = View.GONE
                        }
                    }
                    is ResponseState.Failure -> {
                        binding.alertProgressBar.visibility = View.VISIBLE
                        binding.alertLottiAnimation.visibility = View.GONE
                        binding.txtNoAlerts.visibility = View.GONE
                        binding.recyclerViewAlert.visibility = View.GONE
                        Snackbar.make(binding.root, result.msg.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
        binding.addAlertFloating.setOnClickListener {
            val alertDialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog, null)
            val alertBuilder = AlertDialog.Builder(requireContext()).setView(alertDialogView)
                .setTitle("Setup Alert").setIcon(R.drawable.baseline_add_alarm_24)
            val alertDialog = alertBuilder.show()
            val fromDate: TextView = alertDialogView.findViewById(R.id.from_date)
            val toDate: TextView = alertDialogView.findViewById(R.id.to_date)
            val txtTime: TextView = alertDialogView.findViewById(R.id.time)
            alertDialogView.findViewById<MaterialButton>(R.id.btn_cancel_alert).setOnClickListener {
                alertDialog.dismiss()
            }
            fromDate.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)


                val dpd = DatePickerDialog(
                    requireContext(), { view, year, monthOfYear, dayOfMonth ->

                        var dateString=("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                        fromDate.text =dateString
                        val format=SimpleDateFormat("dd MMM, yyyy")
                        dateFrom=format.parse(dateString).time
                    }, year, month, day
                )

                dpd.show()
            }
            toDate.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)


                val dpd = DatePickerDialog(
                    requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        var dateString=("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                        toDate.text =dateString
                        val format=SimpleDateFormat("dd MMM, yyyy")
                        dateTo=format.parse(dateString).time  },
                    year,
                    month,
                    day
                )

                dpd.show()
            }
            txtTime.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListner = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    cal.set(Calendar.MINUTE, minute)

                    val am_or_pm: String = if (hourOfDay > 12) {
                        "PM"
                    } else {
                        "AM"
                    }
                    txtTime.text = SimpleDateFormat("hh:mm a").format(cal.time)
                    time= convertTimeToLong(SimpleDateFormat("hh:mm a").format(cal.time))
                }
               val tpd= TimePickerDialog(
                    requireContext(),
                    timeSetListner,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    false
                )

                   tpd.show()

            }
            alertDialogView.findViewById<MaterialButton>(R.id.btn_save_alert).setOnClickListener {
                if (txtTime.text != "" && fromDate.text != "" && toDate.text != "") {
                    val roomAlertPojo = RoomAlertPojo(
                        dateFrom = dateFrom,
                        dateTo = dateTo,
                        time = time,
                        countryName = countryName, description = ""
                    )
                    alertViewModel.insertAlert(roomAlertPojo)
                    alertAdapter.notifyDataSetChanged()
                    setupWorker(roomAlertPojo)
                    alertDialog.dismiss()
                    Snackbar.make(
                        binding.root,
                        "The alert added successfully",
                        Snackbar.LENGTH_LONG
                    ).show()

                } else {
                    val alertDialog = AlertDialog.Builder(requireContext())

                    alertDialog.apply {
                        setIcon(R.drawable.info)
                        setTitle("Info")
                        setMessage("You have to set both date and time")
                        setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                        }
                    }.create().show()
                }


            }
        }
    }

    override fun alertCardClick(alertObject: RoomAlertPojo) {

    }

    override fun alertDeleteClick(alertObject: RoomAlertPojo) {
        alertViewModel.deleteAlert(alertObject)
        alertAdapter.notifyDataSetChanged()
    }
    fun setupWorker(roomalert:RoomAlertPojo) {
        val calendar = java.util.Calendar.getInstance()
        val currentTime = convertTimeToLong(getTimeToAlert( calendar.timeInMillis,"en"))
        val targetTime = roomalert.time
        val initialDelay = targetTime-currentTime
        println( getTimeToAlert(currentTime,"en")+currentTime)
        println( getTimeToAlert(roomalert.time,"en")+targetTime)
        println(initialDelay )

        val data = Data.Builder()
        data.putString("address", roomalert.countryName)
        data.putLong("startDate", roomalert.dateFrom)
        data.putLong("endDate", roomalert.dateTo)
        var alert = Gson().toJson(roomalert)
        data.putString("alertWorker", alert)
        val workRequest = PeriodicWorkRequestBuilder<AlertWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(roomalert.dateFrom.toString()+roomalert.dateTo.toString())
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(data.build())
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(roomalert.dateFrom.toString()+roomalert.dateTo.toString(),
            ExistingPeriodicWorkPolicy.REPLACE,workRequest)
    }
    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            alertDialogBuilder.setTitle(getString(R.string.weather_alarm))
                .setMessage(getString(R.string.features))
                .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, i: Int ->
                    var myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(myIntent)
                    dialog.dismiss()
                }.setNegativeButton(
                    getString(R.string.cancel)
                ) { dialog: DialogInterface, i: Int ->
                    dialog.dismiss()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }.show()
        }
    }
}