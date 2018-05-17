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
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import symbbuzz.com.symbbuzzlib.R
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class FCMService: FirebaseMessagingService() {

    var notificationManager: NotificationManager? = null
    var ADMIN_CHANNEL_ID = "23"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        var title: String? = null
        var message: String? = null
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

        clickIntent = Intent(packageName)
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, notificationId, clickIntent,
            PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder: NotificationCompat.Builder

        title = remoteMessage.getData().get("title")
        message = remoteMessage.getData().get("message")

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

        if (bitmap == null) {
            notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_big))
                .setSmallIcon(R.drawable.ic_launcher) //a resource for your custom small icon
                .setContentTitle(title)//the "title" value you sent in your notification
                .setContentText(message) //ditto .setAutoCancel(true) //dismisses the notification on click
                .setColor(resources.getColor(R.color.colorNotification))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
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
        }

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
