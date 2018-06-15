package com.symb.foxpandasdk.ui.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.format.DateFormat
import android.view.View
import android.widget.RemoteViews
import com.google.firebase.messaging.RemoteMessage
import com.symb.foxpandasdk.R
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.services.ClickEventHandler
import com.symb.foxpandasdk.utils.CommonUtils
import java.util.*

internal open class DefaultNotification(var context: Context, var remoteMessage: RemoteMessage) {

    var bitmap: Bitmap? = null
    var title: String? = remoteMessage.data.get(Constants.TITLE)
    var message: String? = remoteMessage.data.get(Constants.CONTENT)
    var activity: String? = remoteMessage.data.get(Constants.CLICK_ACTION)
    var image: String? = remoteMessage.data.get(Constants.MEDIA_URL)

    open fun getInitView(context: Context, notificationId: Int, viewType: String): RemoteViews {
        val views: RemoteViews?
        if(viewType.equals(Constants.SMALL_VIEW))
            views = RemoteViews(context.packageName, R.layout.default_notification_sv)
        else
            views = RemoteViews(context.packageName, R.layout.default_notification_bv)
        configureViews(views)
        initOpenIntent(context, views, notificationId, activity!!)

        return views
    }

    fun configureViews(views: RemoteViews) {
        views.setViewVisibility(R.id.share, View.GONE)
        if (image != null) {
            bitmap = CommonUtils.getBitmapfromUrl(image!!)
            if (bitmap != null) {
                views.setImageViewBitmap(R.id.noti_image, bitmap)
            }
        }
        views.setTextViewText(R.id.noti_time, DateFormat.format("hh:mm A", Calendar.getInstance().getTime()))
        if (title != null)
            views.setTextViewText(R.id.noti_title, title)
        if (message != null)
            views.setTextViewText(R.id.noti_content, message)
    }

    fun initShareIntent(context: Context, views: RemoteViews, shareMessage: String) {
        val shareIntent = Intent(context, ClickEventHandler::class.java)
        shareIntent.action = Constants.OPEN_SHARE
        shareIntent.putExtra(Constants.SHARE_MESSAGE, shareMessage)
        val pShareIntent = PendingIntent.getBroadcast(context, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.share, pShareIntent)
    }

    fun initOpenIntent(context: Context, views: RemoteViews, notificationId: Int, activity: String) {
        val openNotificationIntent = Intent(context, ClickEventHandler::class.java)
        openNotificationIntent.action = Constants.OPEN_ACTIVITY
        openNotificationIntent.putExtra(Constants.NOTIFICATION_ID, notificationId)
        val pOpenNotificationIntent = PendingIntent.getBroadcast(context, notificationId, openNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.noti_ll, pOpenNotificationIntent)
    }

}
