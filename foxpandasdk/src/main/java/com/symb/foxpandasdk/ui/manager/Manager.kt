package com.symb.foxpandasdk.ui.manager

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import com.google.firebase.messaging.RemoteMessage
import com.symb.foxpandasdk.R
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.services.ClickEventHandler
import com.symb.foxpandasdk.ui.notifications.DefaultNotification
import com.symb.foxpandasdk.ui.notifications.YoutubeNotification
import java.util.*

internal class Manager(var context: Context, var remoteMessage: RemoteMessage) {

    var notificationManagerManager: NotificationManager? = null
    var ADMIN_CHANNEL_ID = "23"
    internal lateinit var dbHelper: DBHelper
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    var views: RemoteViews? = null
    var bigViews: RemoteViews? = null
    var clickIntent: Intent? = null

    fun showNotification() {
        var defaultNotification = YoutubeNotification(context, remoteMessage)
        val notificationId = Random().nextInt(60000)
        notificationManagerManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        views = defaultNotification.getInitView(context, notificationId, Constants.SMALL_VIEW)
        bigViews = defaultNotification.getInitView(context, notificationId, Constants.BIG_VIEW)

        clickIntent = Intent(context.packageName)
        clickIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, notificationId, clickIntent,
            PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = buildNotification(context, pendingIntent)
        notificationManagerManager!!.notify(notificationId, notificationBuilder.build())
    }

    private fun buildNotification(context: Context, pendingIntent: PendingIntent): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.app_name))
            .setAutoCancel(true)
            .setOngoing(false)
            .setContentIntent(pendingIntent)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigPictureStyle())
            .setCustomContentView(views)
            .setCustomBigContentView(bigViews)
    }

}
