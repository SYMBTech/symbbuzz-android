package symbbuzz.com.symbbuzzlib.data

data class FirebaseInfo(
    var token: String = "",
    var deviceId: String = "",
    var eventName: String = ""
)

data class ApiResult(
    var message: String = "",
    var code: String = "",
    var status: Boolean = false
)

data class ErrorModel(
    val errorCode: Int,
    val errors: String,
    val error: String,
    val message: String
)
