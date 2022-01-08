package com.memorya.framework.api.translate

import com.memorya.BuildConfig
import com.memorya.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

object TranslateService {
    fun create(): TranslateApi {
        val builder = Retrofit.Builder()
            .baseUrl(Constants.translateBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor(this).build().apply {
                        builder.client(this)
                    }
            }
        }

        return builder.build().create(TranslateApi::class.java)
    }
}