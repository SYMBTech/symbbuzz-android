package com.symb.foxpandasdk.utils

import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.endpoint.Endpoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

internal object NetworkUtil {

    private var endPoints: Endpoint? = null

    internal fun getEndpoint(): Endpoint? = endPoints

    fun initRetrofit(logEnable: Boolean, logLevel: String?) {
        val httpClient: OkHttpClient.Builder
        httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
            request.header("Content-Type", "application/json")
            request.header("Accept", "application/json")
            val requestBuilder = request.build()
            chain.proceed(requestBuilder)
        }
        if(logLevel != null) {
            val logging = HttpLoggingInterceptor()
            logging.level = if(logLevel.equals(Constants.BASIC))
                HttpLoggingInterceptor.Level.BASIC
            else if(logLevel.equals(Constants.HEADER))
                HttpLoggingInterceptor.Level.HEADERS
            else if(logLevel.equals(Constants.NONE))
                HttpLoggingInterceptor.Level.NONE
            else
                HttpLoggingInterceptor.Level.BODY

            if(logEnable)
                httpClient.addInterceptor(logging)
        }
        httpClient.connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        httpClient.readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
        endPoints = retrofit.create(Endpoint::class.java)
    }

}
