package com.example.myapplication.network

import com.example.myapplication.network.dsi.Resource
import com.google.gson.Gson
import com.example.myapplication.network.dsi.ErrorEntity
import com.example.myapplication.network.dsi.ServerErrorEntity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

suspend fun <T> Deferred<Response<T>>.makeRequestAsync(): Resource<T> {
    return withContext(Dispatchers.IO) {
        var resource: Resource<T>
        var headers: Headers? = null
        try {
            val response = this@makeRequestAsync.await()
            headers = response.headers()
            if (response.isSuccessful) {
                resource = successData(response)
            } else {
                resource = errorDataAsync(response)
            }
        } catch (t: Throwable) {
            resource = exceptionDataAsync(t)
        }
        resource.headers = headers
        resource
    }
}

fun exceptionData(t: Throwable): ErrorEntity {
    val errorEntity: ErrorEntity
    if (t is SocketTimeoutException || t is SSLException) {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_CODE_TIMEOUT)
    } else if (t is UnsupportedOperationException && t.message == "JsonNull") {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_CODE_NO_CONTENT)
    } else if (t is UnknownHostException || t is ConnectException) {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_NO_INTERNET)
    } else {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_UNKNOUN_EXEPTION)
        errorEntity.errorDesc = t.message
        t.printStackTrace()
    }
    return errorEntity
}

private fun <T> exceptionDataAsync(t: Throwable): Resource<T> {
    return Resource(Resource.ERROR, null, exceptionData(t))
}

private fun <T> errorData(response: Response<T>): ErrorEntity {
    val gson = Gson()
    val errorEntity: ErrorEntity
    val errorBody = response.errorBody()

    // one shot stream, have to have it externally
    val rawErrorBody = errorBody?.string()
    if (response.code() == 404) {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_CODE_NOT_FOUND)
    } else if (response.code() == 400) {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_CODE_BAD_REQUEST)
    } else if (response.code() == 405) {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_CODE_METHOD_NOT_ALLOWED)
    } else if (response.code() == 401 || response.code() == 403) {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_AUTH_NEEDED)
    } else if (response.code() >= 700 && errorBody != null) {
        val serverErrorEntity = gson.fromJson(rawErrorBody, ServerErrorEntity::class.java)
        errorEntity = serverErrorEntity.toErrorEntity()
        return errorEntity
    } else if (response.code() >= 500) {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_CODE_SERVER_EXCEPTION)
    } else if (response.code() == 422) {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_CODE_422)
    } else {
        errorEntity = ErrorEntity(ErrorEntity.ERROR_UNKNOUN_EXEPTION)
        errorEntity.errorDesc = response.code().toString()
        return errorEntity
    }
    if (errorBody != null) {
        try {
            errorEntity.errorDesc = rawErrorBody
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return errorEntity
}

private fun <T> errorDataAsync(response: Response<T>): Resource<T> {
    return Resource(Resource.ERROR, null, errorData(response))
}

private fun <T> successData(response: Response<T>): Resource<T> {
    val resource: Resource<T> = if (response.code() != 204 && response.body() == null) {
        val errorEntity = ErrorEntity(ErrorEntity.ERROR_CODE_NO_CONTENT)
        Resource(Resource.ERROR, null, errorEntity)
    } else {
        Resource(Resource.SUCCESS, response.body(), null, response)
    }
    return resource
}