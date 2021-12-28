package com.memorya.framework.api.translate

import com.memorya.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TranslateService {
    fun create(): TranslateApi {
        return Retrofit.Builder()
            .baseUrl(Constants.translateBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslateApi::class.java)
    }
}