package com.symb.foxpandasdk.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.app.NotificationManager
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper

class ClickEventHandler: BroadcastReceiver() {

    lateinit var dbHelper: DBHelper

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
            val result = dbHelper.registerEvent(Constants.NOTIFICATION_CLICKED)
            if(result)
                Log.e(Constants.NOTIFICATION_CLICKED, "data successfully logged")
            else
                Log.e(Constants.NOTIFICATION_CLICKED, "data logging failed")
            val firebaseInfo = dbHelper.getAllEvents()
            for(i in firebaseInfo)
                Log.e("event", i.eventName)
            val activity = intent.getStringExtra(Constants.ACTIVITY_NAME)
            val packageName = intent.getStringExtra(Constants.PACKAGE_NAME)
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
            /*if(activity != null) {
                var clickIntent: Intent? = null
                try {
                    val classType = Class.forName(packageName + "." + activity)
                    clickIntent = Intent(context, classType)
                    context.startActivity(clickIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    context.startActivity(Intent(packageName))
                }
            } else
                context.startActivity(Intent(packageName))*/
            closeNotificationTray(context)
        }
    }

    fun closeNotificationTray(context: Context) {
        val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(closeIntent)
    }

}
