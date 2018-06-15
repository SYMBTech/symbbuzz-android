package com.symb.foxpandasdk.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.main.FoxPanda
import com.symb.foxpandasdk.services.FCMIdInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

internal object CommonUtils {

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun registerTokenToServer(token: String, context: Context) {
        val dbHelper = DBHelper(context)
        val params = HashMap<String, String>()
        params.put("token", token)
        params.put("platform", "android")
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.registerToken(params)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                if (result != null) {
                    val result = dbHelper.deleteTokens(token)
                    if(result)
                        Log.e("Yay", "Deleted")
                    else
                        Log.e("Nay", "Noteleted")
                }
            }, this::handleError))
    }

    fun handleError(error: Throwable) {
        when (error) {
            is HttpException -> {
                error.printStackTrace()
            }
            is Exception -> {
                error.printStackTrace()
            }
        }
    }

    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
