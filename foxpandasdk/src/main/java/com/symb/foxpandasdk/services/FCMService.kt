package com.symb.foxpandasdk.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.symb.foxpandasdk.R
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.ui.manager.Manager

class FCMService: FirebaseMessagingService() {

    var notificationManagerManager: android.app.NotificationManager? = null
    var ADMIN_CHANNEL_ID = "23"
    internal lateinit var dbHelper: DBHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        val notificationManager = Manager(this, remoteMessage!!)
        dbHelper = DBHelper(this)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels()
        }
        notificationManager.showNotification()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun setupChannels() {
        val adminChannelName = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManagerManager != null) {
            notificationManagerManager!!.createNotificationChannel(adminChannel)
        }
    }

}
