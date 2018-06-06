package symbbuzz.com.symbbuzzlib.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import symbbuzz.com.symbbuzzlib.app.Application
import java.net.SocketTimeoutException

object CommonUtils {

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun sendDeviceInfoToServer(info: HashMap<String, String>) {
        for(i in info.entries) {
            Log.e(i.key, i.value)
        }
    }

    fun registerTokenToServer(token: String) {
        val params = HashMap<String, String>()
        params.put("token", token)
        params.put("platform", "android")
        compositeDisposable.add(Application.instance.getEndpoint()!!.registerToken(params)
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

    fun closeNotificationTray(context: Context) {
        val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(closeIntent)
    }

}
