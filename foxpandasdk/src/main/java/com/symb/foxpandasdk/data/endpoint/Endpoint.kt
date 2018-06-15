package com.symb.foxpandasdk.data.endpoint

import com.symb.foxpandasdk.data.models.BaseResult
import io.reactivex.Observable
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

internal interface Endpoint {

    @FormUrlEncoded
    @POST("foxpanda-api/public/api/debug/device")
    fun registerToken(@FieldMap param: HashMap<String, String>): Observable<BaseResult>

}
