package com.example.wordsmemory.di

import android.content.Context
import com.example.wordsmemory.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStream

@InstallIn(SingletonComponent::class)
@Module
object CredentialsModule {

    @Provides
    fun provideCredentials(@ApplicationContext appContext: Context): InputStream {
        return appContext.resources.openRawResource(R.raw.wordstranslationcredentials)
    }
}