package symbbuzz.com.symbbuzzlib.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import symbbuzz.com.symbbuzzlib.constants.Constants
import android.R.string.cancel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import com.google.firebase.iid.FirebaseInstanceId
import symbbuzz.com.symbbuzzlib.dbHelper.DBHelper
import symbbuzz.com.symbbuzzlib.utils.CommonUtils


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
                CommonUtils.closeNotificationTray(context)
            }
        } else if(intent.action == Constants.OPEN_ACTIVITY) {
            val result = dbHelper.registerEvent(Constants.NOTIFICATION_CLICKED)
            if(result)
                Log.e(Constants.NOTIFICATION_CLICKED, "data successfully logged")
            else
                Log.e(Constants.NOTIFICATION_CLICKED, "so sad brah")
            val firebaseInfo = dbHelper.getAllEvents()
            for(i in firebaseInfo)
                Log.e("event", i.eventName)
            val activity = intent.getStringExtra(Constants.ACTIVITY_NAME)
            val packageName = intent.getStringExtra(Constants.PACKAGE_NAME)
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
            if(activity != null) {
                var clickIntent: Intent? = null
                try {
                    val classType = Class.forName(packageName + "." + activity)
                    clickIntent = Intent(context, classType)
                    context.startActivity(clickIntent)
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                    //context.startActivity(Intent(packageName))
                }
            } else
                context.startActivity(Intent(packageName))
            CommonUtils.closeNotificationTray(context)
        }
    }
}
