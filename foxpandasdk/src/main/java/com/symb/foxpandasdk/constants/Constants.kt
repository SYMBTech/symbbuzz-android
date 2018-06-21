package com.symb.foxpandasdk.constants

internal object Constants {
    val BASE_URL = "http://35.154.157.5/"
    val OPEN_SHARE = "openshare"
    val OPEN_ACTIVITY = "openactivity"
    val TABLE_NAME = "foxpanda_notification_table"
    val TOKEN_TABLE = "foxpanda_token_table"
    val CLASS_TABLE = "class_table"
    val FIREBASE_TOKEN = "firebase_token"
    val CLASS_NAME = "class_token"
    val DEVICE_ID = "device_id"
    val EVENT_NAME = "event_name"
    val TIMESTAMP = "timestamp"
    val NOTIFICATION_RECEIVED = "notification_received"
    val NOTIFICATION_DISPLAYED = "notification_displayed"
    val NOTIFICATION_CLICKED = "notification_clicked"

    val TITLE = "title"
    val CONTENT = "content"
    val TEMPLATE = "template"
    val MEDIA_TYPE = "mediaType"
    val MEDIA_URL = "mediaUrl"
    val CLICK_ACTION = "clickAction"
    val SHARE_MESSAGE = "shareMessage"
    val SHOW_ICON = "showIcon"
    val SILENT = "silent"

    val DEFAULT_TEMPLATE = "default"

    val NOTIFICATION_ID = "notification_id"
    val ACTIVITY_NAME = "activity_name"
    val PACKAGE_NAME = "package_name"

    val DEFAULT_LOG_LEVEL = "BODY"
    val BASIC = "BASIC"
    val HEADER = "HEADER"
    val NONE = "NONE"

    val BIG_VIEW = "big_view"
    val SMALL_VIEW = "small_view"

    val NOTIFICATION_PATH = "com.symb.foxpandasdk.ui.notifications."

    fun getClassName(template: String): String? {
        when(template) {
            "default" -> {
                return "DefaultNotification"
            }
            "share" -> {
                return "ShareNotification"
            }
        }
        return ""
    }
}
