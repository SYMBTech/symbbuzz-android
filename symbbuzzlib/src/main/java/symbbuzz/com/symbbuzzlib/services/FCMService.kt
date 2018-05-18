package symbbuzz.com.symbbuzzlib.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import symbbuzz.com.symbbuzzlib.R
import symbbuzz.com.symbbuzzlib.constants.Constants
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class FCMService: FirebaseMessagingService() {

    var notificationManager: NotificationManager? = null
    var ADMIN_CHANNEL_ID = "23"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val views = RemoteViews(packageName, R.layout.notification_small_view)
        val bigViews = RemoteViews(packageName, R.layout.notification_big_view)

        var title: String? = null
        var message: String? = null
        var activity: String? = null
        var shareMessage: String? = null
        var image: String? = null
        var clickIntent: Intent? = null
        var bitmap: Bitmap? = null
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Setting up channel for O Devices
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels()
        }

        val notificationId = Random().nextInt(60000)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        if(remoteMessage!!.data != null) {
            image = remoteMessage.data.get("image-url")
            if (image != null)
                bitmap = getBitmapfromUrl(image)
        }

        activity = remoteMessage.getData().get("click_intent")
        Log.e("package name", packageName)
        if(activity != null) {
            Log.e("activity", activity)
            try {
                val classType = Class.forName(packageName + "." + activity)
                clickIntent = Intent(applicationContext, classType)
            } catch (e: ClassNotFoundException) {
                clickIntent = Intent(packageName)
            }
        }
        else
            clickIntent = Intent(packageName)
        clickIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, notificationId, clickIntent,
            PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder: NotificationCompat.Builder

        title = remoteMessage.getData().get("title")
        message = remoteMessage.getData().get("message")
        shareMessage = remoteMessage.data.get("share_msg")

        if (title == null) {
            title = remoteMessage.notification!!.title
        }
        if (message == null) {
            message = remoteMessage.notification!!.body
        }
        if (title == null) {
            title = ""
        }
        if (message == null) {
            message = ""
        }

        /*val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        val pendingShareIntent = PendingIntent.getActivity(this, 0, Intent.createChooser(shareIntent, "share..."),
            PendingIntent.FLAG_UPDATE_CURRENT)
        if (bitmap == null) {
            notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_big))
                .setSmallIcon(R.drawable.ic_launcher) //a resource for your custom small icon
                .setContentTitle(title)//the "title" value you sent in your notification
                .setContentText(message) //ditto .setAutoCancel(true) //dismisses the notification on click
                .setColor(resources.getColor(R.color.colorNotification))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            if(shareMessage != null)
                notificationBuilder.addAction(R.drawable.ic_share, "Share", pendingShareIntent)
        } else {
            val style = NotificationCompat.BigPictureStyle()
            style.bigPicture(bitmap)
            notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_big))
                .setSmallIcon(R.drawable.ic_launcher) //a resource for your custom small icon
                .setContentTitle(title)//the "title" value you sent in your notification
                .setContentText(message) //ditto .setAutoCancel(true) //dismisses the notification on click
                .setColor(resources.getColor(R.color.colorNotification))
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            if(shareMessage != null)
                notificationBuilder.addAction(R.drawable.ic_share, "Share", pendingShareIntent)
        }*/

        if(bitmap != null) {
            views.setImageViewBitmap(R.id.noti_image, bitmap)
            bigViews.setImageViewBitmap(R.id.noti_image, bitmap)
        }
        views.setTextViewText(R.id.noti_title, title)
        bigViews.setTextViewText(R.id.noti_title, title)
        views.setTextViewText(R.id.noti_content, message)
        bigViews.setTextViewText(R.id.noti_content, message)
        Log.e("sharemsg", shareMessage)
        val shareIntent = Intent(applicationContext, ClickEventHandler::class.java)
        shareIntent.action = Constants.OPEN_SHARE
        shareIntent.putExtra("share_msg", shareMessage)
        val pShareIntent = PendingIntent.getBroadcast(this, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        bigViews.setOnClickPendingIntent(R.id.share, pShareIntent)

        /*val openNotificationIntent = Intent(applicationContext, ClickEventHandler::class.java)
        openNotificationIntent.action = Constants.OPEN_ACTIVITY
        openNotificationIntent.putExtra("notification_id", notificationId)
        openNotificationIntent.putExtra("activity_name", activity)
        openNotificationIntent.putExtra("package_name", packageName)
        val pOpenNotificationIntent = PendingIntent.getBroadcast(this, 0, openNotificationIntent, 0)
        views.setOnClickPendingIntent(R.id.noti_image, pOpenNotificationIntent)
        bigViews.setOnClickPendingIntent(R.id.noti_image, pOpenNotificationIntent)*/

        val style = NotificationCompat.BigPictureStyle()
        notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_big))
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.app_name))
            .setAutoCancel(true)
            .setOngoing(false)
            .setContentIntent(pendingIntent)
            .setSound(defaultSoundUri)
            .setStyle(style)
            .setColor(resources.getColor(R.color.colorNotification))
            .setCustomContentView(views)
            .setCustomBigContentView(bigViews)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.notify(notificationId, notificationBuilder.build())
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
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(adminChannel)
        }
    }

    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
