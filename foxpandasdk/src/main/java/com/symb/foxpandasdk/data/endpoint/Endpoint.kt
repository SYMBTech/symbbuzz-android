package com.symb.foxpandasdk.data.endpoint

import com.symb.foxpandasdk.data.models.ApiResult
import io.reactivex.Observable
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Endpoint {

    @FormUrlEncoded
    @POST("device")
    fun registerToken(@FieldMap param: HashMap<String, String>): Observable<ApiResult>

}
