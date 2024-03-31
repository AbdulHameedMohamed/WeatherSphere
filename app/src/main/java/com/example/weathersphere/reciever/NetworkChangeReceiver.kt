package com.example.weathersphere.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class NetworkChangeReceiver : BroadcastReceiver() {
    private val _networkStatus = MutableSharedFlow<Boolean>()

    val networkStatus: SharedFlow<Boolean> = _networkStatus

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val networkInfo = connectivityManager?.activeNetworkInfo
            val isConnected = networkInfo?.isConnectedOrConnecting ?: false
            _networkStatus.tryEmit(isConnected)
        }
    }
}