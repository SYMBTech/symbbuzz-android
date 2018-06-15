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
import com.symb.foxpandasdk.ui.manager.FPNotificationManager

class FCMService: FirebaseMessagingService() {

    internal lateinit var dbHelper: DBHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        val notificationManager = FPNotificationManager(this, remoteMessage!!)
        dbHelper = DBHelper(this)
        notificationManager.showNotification()
    }

}
