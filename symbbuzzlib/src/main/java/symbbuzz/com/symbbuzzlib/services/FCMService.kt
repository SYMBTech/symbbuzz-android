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
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import symbbuzz.com.symbbuzzlib.R
import symbbuzz.com.symbbuzzlib.constants.Constants
import symbbuzz.com.symbbuzzlib.dbHelper.DBHelper
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class FCMService: FirebaseMessagingService() {

    var notificationManager: NotificationManager? = null
    var ADMIN_CHANNEL_ID = "23"
    lateinit var dbHelper: DBHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        dbHelper = DBHelper(this)

        if(remoteMessage!!.data != null) {
            val result = dbHelper.registerEvent(Constants.NOTIFICATION_RECEIVED)
            if(result)
                Log.e(Constants.NOTIFICATION_RECEIVED, "data successfully logged")
            else
                Log.e(Constants.NOTIFICATION_RECEIVED, "so sad brah")

            var views: RemoteViews? = null
            var bigViews: RemoteViews? = null
            var template = remoteMessage.data.get(Constants.TEMPLATE)
            if(template != null) {
                if (template.equals(Constants.DEFAULT_TEMPLATE)) {
                    views = RemoteViews(packageName, R.layout.default_notification_sv)
                    bigViews = RemoteViews(packageName, R.layout.default_notification_bv)
                } else {
                    views = RemoteViews(packageName, R.layout.youtube_notification_sv)
                    bigViews = RemoteViews(packageName, R.layout.youtube_notification_bv)
                }
            } else {
                views = RemoteViews(packageName, R.layout.default_notification_sv)
                bigViews = RemoteViews(packageName, R.layout.default_notification_bv)
            }

            var title: String? = null
            var message: String? = null
            var activity: String? = null
            var shareMessage: String? = null
            var mediaType: String? = null
            var image: String? = null
            var clickIntent: Intent? = null
            var silentString: String? = null
            var silent: Boolean = false
            var bitmap: Bitmap? = null
            var showIcon: Boolean = false
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //Setting up channel for O Devices
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels()
            }

            val notificationId = Random().nextInt(60000)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            image = remoteMessage.data.get(Constants.MEDIA_URL)
            if (image != null)
                bitmap = getBitmapfromUrl(image)

            activity = remoteMessage.getData().get(Constants.CLICK_ACTION)
            Log.e("package name", packageName)
            /*if (activity != null) {
                Log.e("activity", activity)
                try {
                    val classType = Class.forName(packageName + "." + activity)
                    clickIntent = Intent(applicationContext, classType)
                } catch (e: ClassNotFoundException) {
                    clickIntent = Intent(packageName)
                }
            } else*/
                clickIntent = Intent(packageName)
            clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, notificationId, clickIntent,
                PendingIntent.FLAG_ONE_SHOT)

            val notificationBuilder: NotificationCompat.Builder

            title = remoteMessage.getData().get(Constants.TITLE)
            message = remoteMessage.getData().get(Constants.CONTENT)
            shareMessage = remoteMessage.data.get(Constants.SHARE_MESSAGE)
            showIcon = java.lang.Boolean.parseBoolean(remoteMessage.data.get(Constants.SHOW_ICON))
            silent = java.lang.Boolean.parseBoolean(remoteMessage.data.get(Constants.SILENT))

            if (title == null) {
                title = ""
            }
            if (message == null) {
                message = ""
            }
            if (shareMessage == null) {
                shareMessage = ""
                bigViews.setViewVisibility(R.id.share, View.GONE)
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

            views.setTextViewText(R.id.noti_time, DateFormat.format("hh:mm a", Calendar.getInstance().getTime()))
            bigViews.setTextViewText(R.id.noti_time, DateFormat.format("hh:mm a", Calendar.getInstance().getTime()))

            if (bitmap != null) {
                views.setImageViewBitmap(R.id.noti_image, bitmap)
                bigViews.setImageViewBitmap(R.id.noti_image, bitmap)
            }
            views.setTextViewText(R.id.noti_title, title)
            bigViews.setTextViewText(R.id.noti_title, title)
            views.setTextViewText(R.id.noti_content, message)
            bigViews.setTextViewText(R.id.noti_content, message)
            val shareIntent = Intent(applicationContext, ClickEventHandler::class.java)
            shareIntent.action = Constants.OPEN_SHARE
            shareIntent.putExtra(Constants.SHARE_MESSAGE, shareMessage)
            val pShareIntent = PendingIntent.getBroadcast(this, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            bigViews.setOnClickPendingIntent(R.id.share, pShareIntent)

            val openNotificationIntent = Intent(applicationContext, ClickEventHandler::class.java)
            openNotificationIntent.action = Constants.OPEN_ACTIVITY
            openNotificationIntent.putExtra(Constants.NOTIFICATION_ID, notificationId)
            openNotificationIntent.putExtra(Constants.ACTIVITY_NAME, activity)
            openNotificationIntent.putExtra(Constants.PACKAGE_NAME, packageName)
            val pOpenNotificationIntent = PendingIntent.getBroadcast(this, notificationId, openNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.noti_ll, pOpenNotificationIntent)
            bigViews.setOnClickPendingIntent(R.id.noti_image, pOpenNotificationIntent)

            //val icon = packageManager.getApplicationIcon(packageName)
            var icon: Int
            try {
                icon = resources.getIdentifier("ic_launcher", "mipmap", packageName)
            } catch (e: Exception) {
                e.printStackTrace()
                icon = R.drawable.notification_template_icon_bg
            }

            val style = NotificationCompat.BigPictureStyle()
            notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.fox))
                .setSmallIcon(R.drawable.fox)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name))
                .setAutoCancel(true)
                .setOngoing(false)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setCustomContentView(views)
                .setCustomBigContentView(bigViews)

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.notify(notificationId, notificationBuilder.build())
            if(silent)
                notificationManager!!.cancel(notificationId)
            else {
                val result = dbHelper.registerEvent(Constants.NOTIFICATION_DISPLAYED)
                if (result)
                    Log.e(Constants.NOTIFICATION_DISPLAYED, "data successfully logged")
                else
                    Log.e(Constants.NOTIFICATION_DISPLAYED, "so sad brah")
            }
        }
        val firebaseInfo = dbHelper.getAllEvents()
        for(i in firebaseInfo)
            Log.e("event", i.eventName)
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
