package com.example.weathersphere.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

private const val RC_NOTIFICATION_PERMISSION = 123
fun checkNotificationPermission(context: Context) =
    EasyPermissions.hasPermissions(
        context,
        Manifest.permission.ACCESS_NOTIFICATION_POLICY
    )

fun requestNotificationPermission(activity: Activity) {
    EasyPermissions.requestPermissions(
        activity,
        "This app needs notification access permission to function properly.",
        RC_NOTIFICATION_PERMISSION,
        Manifest.permission.ACCESS_NOTIFICATION_POLICY
    )
}