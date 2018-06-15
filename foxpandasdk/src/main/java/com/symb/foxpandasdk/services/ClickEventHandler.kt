package com.symb.foxpandasdk.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.app.NotificationManager
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.main.FoxPanda

class ClickEventHandler: BroadcastReceiver() {

    internal lateinit var dbHelper: DBHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        dbHelper = DBHelper(context!!)
        if(intent!!.action == Constants.OPEN_SHARE) {
            val shareMessage = intent.getStringExtra(Constants.SHARE_MESSAGE)
            if (shareMessage != null) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage)
                val chooserIntent = Intent.createChooser(shareIntent, "Share using")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)
                closeNotificationTray(context)
            }
        } else if(intent.action == Constants.OPEN_ACTIVITY) {
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
            closeNotificationTray(context)
        }
    }

    fun closeNotificationTray(context: Context) {
        val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(closeIntent)
    }

}
