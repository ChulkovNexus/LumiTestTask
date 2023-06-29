package com.rencap.broker.network.dsi

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
        const val NO_ERROR_CODE = ""
        const val ERROR_CODE_TIMEOUT = "ERROR_CODE_TIMEOUT"
        const val ERROR_NO_INTERNET = "ERROR_NO_INTERNET"
        const val ERROR_CODE_NO_CONTENT = "ERROR_CODE_NO_CONTENT"
        const val ERROR_UNKNOUN_EXEPTION = "ERROR_UNKNOUN_EXEPTION"
        const val ERROR_CODE_NOT_FOUND = "ERROR_CODE_NOT_FOUND"
        const val ERROR_CODE_BAD_REQUEST = "ERROR_CODE_BAD_REQUEST"
        const val ERROR_CODE_SERVER_EXCEPTION = "ERROR_CODE_SERVER_EXCEPTION"
        const val ERROR_CODE_METHOD_NOT_ALLOWED = "ERROR_CODE_METHOD_NOT_ALLOWED"
        const val ERROR_AUTH_NEEDED = "ERROR_AUTH_NEEDED"
        const val ERROR_CODE_422 = "ERROR_CODE_422"
        const val ERROR_CODE_700 = "ERROR_CODE_700"
        const val ERROR_CODE_DEVICE_INFO_CHANGED = "DeviceInfoChanged"
        const val ERROR_CODE_DEVICE_INFO_INCOMPLETE = "DeviceInfoIncomplete"
        const val ERROR_CODE_DEVICE_INFO_NOT_SPECIFIED = "DeviceInfoNotSpecified"
        const val ERROR_CODE_SESSION_NOT_FOUND = "SessionNotFound"
        const val UNSUPPORTED_APP_VERSION = "UnsupportedAppVersion"
        const val ERROR_CODE_SESSION_CLOSED = "SessionClosed"
        const val ERROR_CODE_SESSION_TOKEN_NOT_SPECIFIED = "SessionTokenNotSpecified"
        const val INSTRUMENT_NOT_SPECIFIED = "InstrumentNotSpecified"
        const val ERROR_CODE_ACCOUNT_NOT_SPECIFIED = "AccountNotSpecified"
        const val ERROR_CODE_ACCOUNT_NOT_FOUND = "AccountNotFound"
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