package com.example.wordsmemory.di

import android.content.Context
import com.example.wordsmemory.framework.room.WMDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.InternalCoroutinesApi

@InstallIn(SingletonComponent::class)
@Module
object DbModule {

    @InternalCoroutinesApi
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        WMDatabase.getInstance(appContext)

    @Provides
    fun provideVocabularyItemDao(database: WMDatabase) = database.vocabularyItemDao()

    @Provides
    fun provideCategoryDao(database: WMDatabase) = database.categoryDao()

    @Provides
    fun provideUserDao(database: WMDatabase) = database.userDao()

    @Provides
    fun provideFirestoreDb() = Firebase.firestore
}