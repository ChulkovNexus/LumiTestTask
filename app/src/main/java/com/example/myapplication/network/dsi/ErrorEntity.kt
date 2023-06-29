package com.example.myapplication.network.dsi

import android.text.TextUtils

class ErrorEntity : Throwable {
    var errorCode: String? = null
    var errorDesc: String? = null
    var errorAdditionalInfoForCode700: String? = null

    constructor()
    constructor(errorCode: String?) {
        this.errorCode = errorCode
    }

    val isError: Boolean
        get() = !TextUtils.isEmpty(errorCode)

    val text: String
        get() = if (TextUtils.isEmpty(errorDesc)) errorCode + "" else "$errorDesc"

    companion object {
        const val ERROR_CODE_TIMEOUT = "ERROR_CODE_TIMEOUT"
        const val ERROR_NO_INTERNET = "ERROR_NO_INTERNET"
        const val ERROR_CODE_NO_CONTENT = "ERROR_CODE_NO_CONTENT"
        const val ERROR_CODE_STATUS_0 = "ERROR_CODE_STATUS_0"
        const val ERROR_UNKNOUN_EXEPTION = "ERROR_UNKNOUN_EXEPTION"
        const val ERROR_CODE_NOT_FOUND = "ERROR_CODE_NOT_FOUND"
        const val ERROR_CODE_BAD_REQUEST = "ERROR_CODE_BAD_REQUEST"
        const val ERROR_CODE_SERVER_EXCEPTION = "ERROR_CODE_SERVER_EXCEPTION"
        const val ERROR_CODE_METHOD_NOT_ALLOWED = "ERROR_CODE_METHOD_NOT_ALLOWED"
        const val ERROR_AUTH_NEEDED = "ERROR_AUTH_NEEDED"
        const val ERROR_CODE_422 = "ERROR_CODE_422"
        const val ERROR_CODE_700 = "ERROR_CODE_700"
    }
}

data class ServerErrorEntity(
    val error: ServerError
) {
    fun toErrorEntity(): ErrorEntity {
        val result = ErrorEntity()
        result.errorCode = ErrorEntity.ERROR_CODE_700
        result.errorDesc = error.Errortext
        result.errorAdditionalInfoForCode700 = error.ErrorCode
        return result
    }
}

data class ServerError(
    val ErrorCode: String,
    val Errortext: String
)