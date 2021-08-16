package com.example.wordsmemory.api.auth

import com.example.wordsmemory.model.AuthResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("token")
    suspend fun auth(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") authCode: String,
        @Query("grant_type") grantType: String = "authorization_code",
        @Query("redirect_uri") redirectUri: String = "urn:ietf:wg:oauth:2.0:oob"
    ): Response<AuthResponse>
}