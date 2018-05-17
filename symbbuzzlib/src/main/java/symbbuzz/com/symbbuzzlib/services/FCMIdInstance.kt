package symbbuzz.com.symbbuzzlib.services

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import symbbuzz.com.symbbuzzlib.utils.CommonUtils

class FCMIdInstance: FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e("refreshtoken", refreshedToken)
        CommonUtils.sendTokenToServer(refreshedToken!!)
    }
}
