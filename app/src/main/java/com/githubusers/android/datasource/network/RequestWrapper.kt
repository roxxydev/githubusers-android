package com.githubusers.android.datasource.network

import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException

/**
 * Call Retrofit service which then wraps the response to specific type to allow
 * error response be handled specifically to its type.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResultWrapper<T> {
    return try {
        ResultWrapper.Success(apiCall.invoke())
    } catch (exception: Exception) {
        when (exception) {
            is IOException -> ResultWrapper.NetworkError
            is HttpException -> {
                val code = exception.code()
                val errorResponse =
                    convertErrorBody(exception)
                ResultWrapper.GenericError(
                    code,
                    errorResponse
                )
            }
            else -> {
                ResultWrapper.GenericError(
                    null,
                    null
                )
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            val moshiAdapter = Moshi.Builder()
                .build()
                .adapter(ErrorResponse::class.java)
            moshiAdapter.fromJson(it)
        }
    } catch (exception: Exception) {
        null
    }
}

data class ErrorResponse(
    val errors: List<String>
)

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class GenericError(val code: Int? = null, val error: ErrorResponse? = null): ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()
}
