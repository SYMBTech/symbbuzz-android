package com.symb.foxpandasdk.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.symb.foxpandasdk.utils.CommonUtils
import com.symb.foxpandasdk.utils.NetworkUtil

class FCMIdInstance: FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        NetworkUtil.initRetrofit()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e("refreshtoken", refreshedToken)
        CommonUtils.registerTokenToServer(refreshedToken!!)
    }
}
