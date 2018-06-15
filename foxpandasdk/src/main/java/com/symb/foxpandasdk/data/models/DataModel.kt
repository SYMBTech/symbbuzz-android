package com.symb.foxpandasdk.data.models

internal data class FirebaseInfo(
    var token: String = "",
    var deviceId: String = "",
    var eventName: String = ""
)

internal data class BaseResult(
    var message: String = "",
    var code: String = "",
    var status: Boolean = false
)

internal data class ErrorModel(
    val errorCode: Int,
    val errors: String,
    val error: String,
    val message: String
)
