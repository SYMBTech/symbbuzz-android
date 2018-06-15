package com.symb.foxpandasdk.utils

import com.google.firebase.messaging.RemoteMessage
import com.symb.foxpandasdk.constants.Constants

internal class RemoteViewsUtils(remoteMessage: RemoteMessage) {
    var title: String? = remoteMessage.data.get(Constants.TITLE)
    var message: String? = remoteMessage.data.get(Constants.CONTENT)
    var activity: String? = remoteMessage.data.get(Constants.CLICK_ACTION)
    var shareMessage: String? = remoteMessage.data.get(Constants.SHARE_MESSAGE)
    var mediaType: String? = remoteMessage.data.get(Constants.MEDIA_TYPE)
    var image: String? = remoteMessage.data.get(Constants.MEDIA_URL)
    var silent: Boolean = java.lang.Boolean.parseBoolean(remoteMessage.data.get(Constants.SILENT))
    var showIcon: Boolean = java.lang.Boolean.parseBoolean(remoteMessage.data.get(Constants.SHOW_ICON))
    var groupValue: String? = remoteMessage.data.get("groupValue")
}
