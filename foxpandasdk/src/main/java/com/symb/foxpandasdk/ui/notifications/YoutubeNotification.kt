package com.symb.foxpandasdk.ui.notifications

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.google.firebase.messaging.RemoteMessage
import com.symb.foxpandasdk.R
import com.symb.foxpandasdk.constants.Constants

internal class YoutubeNotification(context: Context, remoteMessage: RemoteMessage) : DefaultNotification(context, remoteMessage) {

    override fun getInitView(context: Context, notificationId: Int, viewType: String): RemoteViews {
        var views: RemoteViews? = null
        if(viewType.equals(Constants.SMALL_VIEW))
            views = RemoteViews(context.packageName, R.layout.default_notification_sv)
        else
            views = RemoteViews(context.packageName, R.layout.default_notification_bv)

        super.configureViews(views)

        if(super.remoteViewsUtils.shareMessage != null) {
            views.setViewVisibility(R.id.share, View.VISIBLE)
            initShareIntent(context, views, remoteViewsUtils.shareMessage!!)
        }
        else
            views.setViewVisibility(R.id.share, View.GONE)

        return views
    }

}
