package com.example.wordsmemory.di

import android.content.Context
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object GoogleSignInModule {

    @Provides
    fun provideClient(@ApplicationContext appContext: Context): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(Constants.webClientId)
            .requestScopes(
                Scope(appContext.getString(R.string.cloud_platform_scope)),
                Scope(appContext.getString(R.string.cloud_translation_scope))
            )
            .build()
        return GoogleSignIn.getClient(appContext, signInOptions)
    }

    @Provides
    fun getSignedInAccount(@ApplicationContext appContext: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(appContext)
    }
}