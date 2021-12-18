package com.example.wordsmemory.framework.api

import com.example.wordsmemory.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

abstract class ResultProvider {
    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = call()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Timber.i("Network call successful")
                        return@withContext Result.Success(body)
                    }

                    return@withContext error(null, "Error! Body is null!")
                }

                return@withContext error(response.code(), response.message())
            } catch (e: Exception) {
                return@withContext error(null, "Exception: ${e.message ?: e.toString()}")
            }
        }
    }

    private fun <T> error(code: Int? = null, message: String): Result<T> {
        if (code != null) Timber.e("$code - $message") else Timber.e(message)
        return Result.Error(code, message)
    }
}