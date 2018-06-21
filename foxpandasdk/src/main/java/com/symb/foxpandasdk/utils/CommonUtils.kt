package com.symb.foxpandasdk.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.main.FoxPanda
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.net.URL
import dalvik.system.DexFile
import java.io.IOException


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
                    val dbResult = dbHelper.deleteTokens(token)
                    if(dbResult)
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

    fun getClassesOfPackage(context: Context): Array<String> {
        val classes = ArrayList<String>()
        FoxPanda.FPLogger("package", context.packageName)
        try {
            val packageCodePath = context.packageCodePath
            @Suppress("DEPRECATION")
            val df = DexFile(packageCodePath)
            val iter = df.entries()
            while (iter.hasMoreElements()) {
                val className = iter.nextElement()
                if (className.contains(context.packageName)) {
                    classes.add(className)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return classes.toArray(arrayOfNulls(classes.size))
    }

}
