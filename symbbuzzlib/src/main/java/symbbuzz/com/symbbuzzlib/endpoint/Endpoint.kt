package symbbuzz.com.symbbuzzlib.endpoint

import io.reactivex.Observable
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import symbbuzz.com.symbbuzzlib.data.ApiResult

interface Endpoint {

    @FormUrlEncoded
    @POST("device")
    fun registerToken(@FieldMap param: HashMap<String, String>): Observable<ApiResult>

}
