package symbbuzz.com.symbbuzz

import android.provider.Settings
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import symbbuzz.com.symbbuzzlib.BuildConfig

class Application : android.app.Application() {
    companion object {
        lateinit var instance: Application
    }

    var clientId = "c737bd3709dafc01e5d90e730841f8f46e0bcb00"
    var clientSecret = "v/AyXou894ZaveojECsScUDGuCpNezl3wra/Wyq2nkFo+u7GK9E9HpPmpqTisUDtiJ01v/AWkbIyqEIUDSK9uVx2wU2esLj582/9k9OCIKqbtFdv5fMlQf4kbeNhotwv"
    var SCOPE = "public private"
    var accessToken = "743c1f1f5a72cc6d12f3b82052ee5725"

    override fun onCreate() {
        super.onCreate()
        instance = this

        /*val configBuilder = Configuration.Builder(clientId, clientSecret, SCOPE)
            .setCacheDirectory(this.cacheDir)
        VimeoClient.initialize(configBuilder.build())*/
    }

}
