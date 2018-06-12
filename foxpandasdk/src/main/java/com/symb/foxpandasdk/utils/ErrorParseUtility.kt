package com.symb.foxpandasdk.utils

import com.symb.foxpandasdk.data.models.ErrorModel
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import java.io.IOException


/**
 * Created by zultanite on 1/18/2018.
 */
object ErrorParseUtility {
    fun parseError(response: Response<*>): ErrorModel? {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://35.154.157.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val converter: Converter<ResponseBody, ErrorModel> = retrofit.responseBodyConverter(ErrorModel::class.java, arrayOfNulls<Annotation>(0))

        var errorModel: ErrorModel? = null

        try {
            errorModel = converter.convert(response.errorBody())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return errorModel
    }
}
