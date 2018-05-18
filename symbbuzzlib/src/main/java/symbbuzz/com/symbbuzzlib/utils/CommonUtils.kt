package symbbuzz.com.symbbuzzlib.utils

import android.content.Context
import android.content.Intent
import android.util.Log

object CommonUtils {

    fun sendDeviceInfoToServer(info: HashMap<String, String>) {
        for(i in info.entries) {
            Log.e(i.key, i.value)
        }
    }

}
