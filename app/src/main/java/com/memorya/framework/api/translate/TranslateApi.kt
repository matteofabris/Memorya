package com.memorya.framework.api.translate

import com.memorya.BuildConfig
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslateApi {
    @POST("language/translate/v2")
    suspend fun translate(
        @Query("q") inputText: String,
        @Query("source") sourceLanguage: String = "en",
        @Query("target") targetLanguage: String = "it",
        @Query("format") format: String = "text",
        @Query("key") key: String = BuildConfig.API_KEY
    ): Response<TranslateResponse>
}