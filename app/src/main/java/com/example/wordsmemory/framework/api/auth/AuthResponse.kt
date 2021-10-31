package com.example.wordsmemory.framework.api.auth

import com.google.gson.annotations.SerializedName

class AuthResponse {
    @SerializedName("access_token")
    var accessToken: String = ""
    @SerializedName("expires_in")
    var expiresIn: String = ""
    @SerializedName("id_token")
    var idToken: String = ""
    @SerializedName("refresh_token")
    var refreshToken: String = ""
    @SerializedName("scope")
    var scope: String = ""
    @SerializedName("token_type")
    var tokenType: String = ""
}