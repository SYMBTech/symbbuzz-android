package com.symb.foxpandasdk.utils

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.SocketTimeoutException

object CommonUtils {

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun sendDeviceInformationToServer(info: HashMap<String, String>) {
        for(i in info.entries) {
            Log.e(i.key, i.value)
        }
    }

    fun registerTokenToServer(token: String) {
        val params = HashMap<String, String>()
        params.put("token", token)
        params.put("platform", "android")
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.registerToken(params)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                if (result != null) {

                }
            }, this::handleError))
    }

    fun handleError(error: Throwable) {
        when (error) {
            is SocketTimeoutException -> {
            }
            is HttpException -> {
            }
            is Exception -> {
            }
            else -> {
            }
        }
    }

}
