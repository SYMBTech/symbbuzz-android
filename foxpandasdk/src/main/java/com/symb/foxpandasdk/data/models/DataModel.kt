package com.symb.foxpandasdk.data.models

import com.symb.foxpandasdk.main.FoxPanda

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
    var errorCode: Int,
    var errors: String,
    var error: String,
    var message: String
)
