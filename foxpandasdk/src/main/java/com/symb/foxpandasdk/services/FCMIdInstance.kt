package com.symb.foxpandasdk.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.main.FoxPanda
import com.symb.foxpandasdk.utils.CommonUtils
import com.symb.foxpandasdk.utils.NetworkUtil

class FCMIdInstance: FirebaseInstanceIdService() {
    internal lateinit var dbHelper: DBHelper
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        dbHelper = DBHelper(this)
        NetworkUtil.initRetrofit(true, Constants.DEFAULT_LOG_LEVEL)
        val refreshedToken = FirebaseInstanceId.getInstance().token
        val bool = dbHelper.registerToken(refreshedToken!!)
        if(bool)
            FoxPanda.FPLogger("Yay", "Registered")
        val token = dbHelper.getToken()
        CommonUtils.registerTokenToServer(refreshedToken, this)
        Log.e("dbtoken", token)
        Log.e("refreshtoken", refreshedToken)
    }
}
