package com.example.wordsmemory.api.translate

import com.example.wordsmemory.Constants
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