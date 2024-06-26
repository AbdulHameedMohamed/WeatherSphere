package com.example.weathersphere.reciever

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import com.example.weathersphere.R
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSourceImpl
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSourceImpl
import com.example.weathersphere.utils.Constants
import com.example.weathersphere.utils.NetworkManager
import com.example.weathersphere.utils.NotificationManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val alarm = intent?.getSerializableExtra(Constants.WEATHER_ALARM) as WeatherAlarm

        var messageFromApi = "The weather has cleared up and conditions are now good"

        CoroutineScope(Dispatchers.IO).launch {
            WeatherLocalDataSourceImpl(DatabaseProvider.getDatabase(context).weatherDao)
                .deleteAlarm(alarm)

            val apiMessage = getAlertMessageFromApi(context, alarm)

            if (!apiMessage.isNullOrBlank()) {
                messageFromApi = apiMessage
            }

            withContext(Dispatchers.Main) {
                when (alarm.kind) {
                    Constants.NOTIFICATION -> createNotification(
                        context,
                        messageFromApi,
                        alarm.zoneName
                    )

                    Constants.ALERT -> {
                        createAlertDialog(
                            context,
                            messageFromApi,
                            alarm.zoneName
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun createNotification(context: Context, messageFromApi: String, zoneName: String) {
        val builder = NotificationManager.createNotification(context, messageFromApi, zoneName)
        with(NotificationManagerCompat.from(context)) {
            notify(Constants.NOTIFICATION_ID, builder.build())
        }
        val mediaPlayer = MediaPlayer.create(context, R.raw.pop_up)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
    }

    private fun createAlertDialog(context: Context, message: String, zoneName: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_alarm, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.alert_description)
        val zoneNameTV = dialogView.findViewById<TextView>(R.id.zoneName)
        val dialogOkButton = dialogView.findViewById<Button>(R.id.alert_stop)

        zoneNameTV.text = zoneName
        dialogMessage.text = message

        val mediaPlayer = MediaPlayer.create(context, R.raw.alert)

        val builder = AlertDialog.Builder(context, R.style.Base_Theme_WeatherSphere)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setOnShowListener {
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }

        val window = dialog.window
        window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.CENTER)
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        dialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(15000)
                withContext(Dispatchers.Main) {
                    if (dialog.isShowing) {
                        dialog.dismiss()
                        createNotification(context, message, zoneName)
                    }
                }
            } finally {
                cancel()
            }
        }

        dialogOkButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            mediaPlayer.stop()
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }
        }
    }

    private suspend fun getAlertMessageFromApi(
        context: Context,
        weatherAlarm: WeatherAlarm
    ): String? {
        var message: String? = null
        try {
            if (NetworkManager.checkConnection(context)) {
                val weatherResponse = WeatherRemoteDataSourceImpl(RetrofitClient.apiService)
                    .getWeather(LatLng(weatherAlarm.latitude, weatherAlarm.longitude), "en")

                if (weatherResponse.isSuccessful)
                    message = weatherResponse.body()?.alerts?.get(0)?.description ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return message
    }
}