package com.memorya.framework.api.auth

import com.memorya.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthService {
    fun create(): AuthApi {
        return Retrofit.Builder()
            .baseUrl(Constants.authBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}