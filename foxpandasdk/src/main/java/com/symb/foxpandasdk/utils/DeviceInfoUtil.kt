package com.symb.foxpandasdk.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import java.net.NetworkInterface
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.os.StatFs
import android.provider.Settings
import android.support.v4.os.ConfigurationCompat
import java.util.*
import android.telephony.TelephonyManager
import java.io.File
import java.text.SimpleDateFormat
import com.facebook.device.yearclass.YearClass
import com.google.firebase.iid.FirebaseInstanceId
import com.symb.foxpandasdk.BuildConfig
import kotlin.collections.HashMap

internal object DeviceInfoUtil {

    fun getDeviceInfo(context: Context): HashMap<String, String> {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val param = HashMap<String, String>()
        param["app_version"] = BuildConfig.VERSION_NAME
        param["device_id"] = android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            param["d_internal_storage"] = totalMemory().toString()
            param["d_free_storage"] = availableMemory().toString()
        }
        param["d_ip_address"] = getIp(context).toString()
        param["locale"] = Locale.getDefault().toString()
        param["d_manufacturer"] = Build.MANUFACTURER
        param["d_model"] = Build.MODEL
        param["d_product"] = Build.PRODUCT
        param["d_ram"] = ramSize(context).toString()
        param["d_type"] = "android"
        if(DeviceInfoUtil.isRooted()) {
            param["d_root"] = "1"
        } else {
            param["d_root"] = "0"
        }
        param["d_year"] = yearClass(context).toString()
        param["internet_service_provider"] = serviceProvider(context)
        param["network_type"] = networkTpye(context).toString()
        if(networkTpye(context).toString().equals("Unknown"))
            param["country"] = Locale.getDefault().country
        else
            param["country"] = tm.getSimCountryIso()
        param["d_sdk"] = Build.VERSION.RELEASE
        param["timezone"] = timeZone()
        param["user_language"] = Locale.getDefault().getDisplayLanguage()
        param["token"] = FirebaseInstanceId.getInstance().token.toString()
        return param
    }

    fun getIp(context: Context): String? {
        var WIFI = false
        var MOBILE = false
        val CM = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = CM.allNetworkInfo
        for (netInfo in networkInfo) {
            if (netInfo.typeName.equals("WIFI", ignoreCase = true))
                if (netInfo.isConnected)
                    WIFI = true
            if (netInfo.typeName.equals("MOBILE", ignoreCase = true))
                if (netInfo.isConnected)
                    MOBILE = true
        }
        if (WIFI) {
            return GetDeviceipWiFiData(context)
        }
        return if (MOBILE) {
            GetDeviceipMobileData()
        } else null
    }

    fun GetDeviceipMobileData(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkinterface = en.nextElement()
                val enumIpAddr = networkinterface.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("Current IP", ex.toString())
        }
        return null
    }

    fun GetDeviceipWiFiData(context: Context): String {
        val wm = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        return ip
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun totalMemory(): Long {
        val stat = StatFs(Environment.getDataDirectory().getPath())
        val available = stat.blockSize * stat.blockCountLong
        return available / (1024 * 1024)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun availableMemory(): Long {
        val stat = StatFs(Environment.getDataDirectory().getPath())
        val available = stat.blockSize * stat.availableBlocksLong
        return available / (1024 * 1024)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun ramSize(context: Context): Long {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        val memoryInfo = ActivityManager.MemoryInfo()
        assert(activityManager != null)
        activityManager!!.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem / (1024 * 1024)
    }

    fun networkTpye(context: Context): String? {
        val teleMan = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val network = teleMan.networkType
        var network_type: String? = null
        when (network) {
            TelephonyManager.NETWORK_TYPE_1xRTT -> network_type = "1xRTT"
            TelephonyManager.NETWORK_TYPE_CDMA -> network_type = "CDMA"
            TelephonyManager.NETWORK_TYPE_EDGE -> network_type = "EDGE"
            TelephonyManager.NETWORK_TYPE_EHRPD -> network_type = "eHRPD"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> network_type = "EVDO rev. 0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> network_type = "EVDO rev. A"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> network_type = "EVDO rev. B"
            TelephonyManager.NETWORK_TYPE_GPRS -> network_type = "GPRS"
            TelephonyManager.NETWORK_TYPE_HSDPA -> network_type = "HSDPA"
            TelephonyManager.NETWORK_TYPE_HSPA -> network_type = "HSPA"
            TelephonyManager.NETWORK_TYPE_HSPAP -> network_type = "HSPA+"
            TelephonyManager.NETWORK_TYPE_HSUPA -> network_type = "HSUPA"
            TelephonyManager.NETWORK_TYPE_IDEN -> network_type = "iDen"
            TelephonyManager.NETWORK_TYPE_LTE -> network_type = "LTE"
            TelephonyManager.NETWORK_TYPE_UMTS -> network_type = "UMTS"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> network_type = "Unknown"
        }
        return network_type
    }

    fun timeZone(): String {
        val timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault())
        val currentLocalTime = calendar.time
        val date = SimpleDateFormat("z", Locale.getDefault())
        val localTime = date.format(currentLocalTime)
        return localTime + "  " + timeZone.id
    }

    fun serviceProvider(context: Context): String {
        val teleMan = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return teleMan.networkOperatorName
    }

    fun isRooted(): Boolean {

        // get from build info
        val buildTags = android.os.Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        // check if /system/app/Superuser.apk is present
        try {
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                return true
            }
        } catch (e1: Exception) {
            // ignore
        }

        // try executing commands
        return (canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su"))
    }

    private fun canExecuteCommand(command: String): Boolean {
        var executedSuccesfully: Boolean
        try {
            Runtime.getRuntime().exec(command)
            executedSuccesfully = true
        } catch (e: Exception) {
            executedSuccesfully = false
        }

        return executedSuccesfully
    }


    fun yearClass(context: Context): Int {
        return YearClass.get(context.applicationContext)
    }

}
