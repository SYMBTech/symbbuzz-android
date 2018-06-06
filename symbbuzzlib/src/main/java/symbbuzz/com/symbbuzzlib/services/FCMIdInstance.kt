package symbbuzz.com.symbbuzzlib.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import symbbuzz.com.symbbuzzlib.utils.CommonUtils
import symbbuzz.com.symbbuzzlib.utils.DeviceInfoUtil

class FCMIdInstance: FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e("refreshtoken", refreshedToken)
        //val deviceInfo = DeviceInfoUtil.getDeviceInfo(this)
        CommonUtils.registerTokenToServer(refreshedToken!!)
    }
}
