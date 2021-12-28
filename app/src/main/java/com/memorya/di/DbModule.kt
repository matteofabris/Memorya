package com.memorya.di

import android.content.Context
import com.memorya.framework.room.MyDatabase
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
        MyDatabase.getInstance(appContext)

    @Provides
    fun provideVocabularyItemDao(database: MyDatabase) = database.vocabularyItemDao()

    @Provides
    fun provideCategoryDao(database: MyDatabase) = database.categoryDao()

    @Provides
    fun provideUserDao(database: MyDatabase) = database.userDao()

    @Provides
    fun provideFirestoreDb() = Firebase.firestore
}