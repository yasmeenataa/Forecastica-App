package com.example.forecasticaapp.alerts.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.forecasticaapp.R
import com.example.forecasticaapp.database.ConcreteLocalSource
import com.example.forecasticaapp.database.LocalSource
import com.example.forecasticaapp.database.RoomAlertPojo
import com.example.forecasticaapp.database.WeatherDatabase
import com.example.forecasticaapp.databinding.AlarmDialogBinding
import com.example.forecasticaapp.homePage.viewModel.HomeViewModel
import com.example.forecasticaapp.homePage.viewModel.HomeViewModelFactory
import com.example.forecasticaapp.models.Alert
import com.example.forecasticaapp.models.OneCallResponse
import com.example.forecasticaapp.models.Repository
import com.example.forecasticaapp.network.ApiClient
import com.example.forecasticaapp.network.ResponseState
import com.example.forecasticaapp.utils.Constants
import com.example.forecasticaapp.utils.convertDateToLong
import com.example.forecasticaapp.utils.getDateToAlert
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.security.auth.login.LoginException

class AlertWorker(private var appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    val sharedPreferences = appContext.getSharedPreferences(
        Constants.SHARED_PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )
    val lat: Double? = sharedPreferences.getString(Constants.GPS_LAT, "")?.toDouble()
    val lon: Double? = sharedPreferences.getString(Constants.GPS_LON, "")?.toDouble()
    val language: String? = sharedPreferences.getString(Constants.LANGUAGE, "en")
    val units: String? =
        sharedPreferences.getString(Constants.UNITS, Constants.ENUM_UNITS.standard.toString())
    var _repo = Repository.getInstance(
        ApiClient.getInstance(),
        ConcreteLocalSource(appContext)
    )


    companion object {
        const val CHANNEL_ID = "channelID"
    }


    @SuppressLint("SuspiciousIndentation")
    override suspend fun doWork(): Result {
        var systemTime = System.currentTimeMillis()
        systemTime = convertDateToLong(getDateToAlert(systemTime, "en"))
        val alertWorker = inputData.getString("alertWorker")
        var roomAlert = Gson().fromJson(alertWorker, RoomAlertPojo::class.java)
        if (systemTime >= roomAlert.dateFrom && systemTime <= roomAlert.dateTo) {
            var oneCallResponse: OneCallResponse
            val apiClient = ApiClient.getInstance()
            runBlocking {
                oneCallResponse = apiClient.getOneCallResponse(lat, lon, units, language)
            }

            var desc: String = oneCallResponse.alerts?.get(0)?.event
                ?: appContext.getString(R.string.no_alert_weather_is_fine)
            if (desc == "") desc = appContext.getString(R.string.no_alert_weather_is_fine)

            if (sharedPreferences.getString(
                    Constants.ALERT_TYPE,
                    Constants.Enum_ALERT.NOTIFICATION.toString()
                ) == Constants.Enum_ALERT.NOTIFICATION.toString()
            ) {
                sharedPreferences.getString(Constants.COUNTRY_NAME, "")
                    ?.let { setupNotification(it, desc) }
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    sharedPreferences.getString(Constants.COUNTRY_NAME, "")
                        ?.let { SetupAlarm(appContext, desc, it).onCreate() }
                }
            }
            return Result.success()
        } else {
            Log.i(
                "TAG",
                "doWork:systemTime: $systemTime  \n roomAlert.dateTo: ${roomAlert.dateTo} \n roomAlert.time: ${roomAlert.time}"
            )
            if (systemTime >= roomAlert.dateTo && systemTime >= roomAlert.time) {
                _repo.deleteAlert(roomAlert)
                WorkManager.getInstance(appContext)
                    .cancelAllWorkByTag(roomAlert.dateFrom.toString() + roomAlert.dateTo.toString())
                return Result.success()
            } else {
                return Result.failure()
            }
        }
    }

    private fun setupNotification(timezone: String, descriptions: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(timezone)
            .setContentText(descriptions)
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }

    class SetupAlarm(
        private val context: Context,
        private val description: String,
        private val country: String
    ) {
        lateinit var binding: AlarmDialogBinding
        private lateinit var customDialog: View
        private var mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.alarm)


        fun onCreate() {
            mediaPlayer.start()// no need to call prepare(); create() does that for you
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            customDialog = inflater.inflate(R.layout.alarm_dialog, null)
            binding = AlarmDialogBinding.bind(customDialog)
            initView()
            val LAYOUT_FLAG: Int = Flag()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val layoutParams: WindowManager.LayoutParams = Params(LAYOUT_FLAG)
            windowManager.addView(customDialog, layoutParams)

        }

        private fun Flag(): Int {
            val LAYOUT_FLAG: Int
            LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            return LAYOUT_FLAG
        }

        private fun Params(LAYOUT_FLAG: Int): WindowManager.LayoutParams {
            val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
            return WindowManager.LayoutParams(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
                PixelFormat.TRANSLUCENT
            )
        }

        private fun initView() {
            // binding.img.setImageResource(R.drawable.ic_broken_cloud)
            binding.txtAlarmDesc.text = description
            binding.txtAlarmCountry.text = country
            binding.btnOkAlarm.setOnClickListener {
                close()
            }
        }

        private fun close() {
            try {
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(
                    customDialog
                )
                customDialog.invalidate()
                (customDialog.parent as ViewGroup).removeAllViews()
            } catch (e: Exception) {
                Log.d("Error", e.toString())
            }
            mediaPlayer.release()

        }


    }

}
