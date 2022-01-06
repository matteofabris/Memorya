package com.memorya.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
            .build()
        return GoogleSignIn.getClient(appContext, signInOptions)
    }

    @Provides
    fun getSignedInAccount(@ApplicationContext appContext: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(appContext)
    }
}