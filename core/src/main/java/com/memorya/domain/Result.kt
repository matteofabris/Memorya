package com.memorya.domain

sealed class Result<T> {
    class Success<T>(val data: T) : Result<T>()
    class Error<T>(val code: Int? = null, val message: String) : Result<T>()
    class Loading<T>(val data: T? = null) : Result<T>()
}