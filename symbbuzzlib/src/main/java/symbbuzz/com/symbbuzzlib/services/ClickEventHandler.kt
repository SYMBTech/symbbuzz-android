package symbbuzz.com.symbbuzzlib.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import symbbuzz.com.symbbuzzlib.constants.Constants
import android.R.string.cancel
import android.app.NotificationManager



class ClickEventHandler: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action == Constants.OPEN_SHARE) {
            val shareMessage = intent.getStringExtra("share_msg")
            if (shareMessage != null) {
                Log.e("share_msg", shareMessage)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage)
                context!!.startActivity(Intent.createChooser(shareIntent, "Share using"))
                val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.sendBroadcast(closeIntent)
            }
        } else if(intent.action == Constants.OPEN_ACTIVITY) {
            val activity = intent.getStringExtra("activity_name")
            val packageName = intent.getStringExtra("package_name")
            if(activity != null) {
                var clickIntent: Intent? = null
                try {
                    val classType = Class.forName(packageName + "." + activity)
                    clickIntent = Intent(context, classType)
                } catch (e: ClassNotFoundException) {
                    clickIntent = Intent(packageName)
                }
                context!!.startActivity(clickIntent)
            }
        }
    }
}
