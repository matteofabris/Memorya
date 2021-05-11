package com.example.wordsmemory.di

import android.content.Context
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.database.VocabularyDatabase
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
    fun provideDatabase(@ApplicationContext appContext: Context): VocabularyDatabase {
        return VocabularyDatabase.getInstance(appContext)
    }

    @Provides
    fun provideVocabularyDao(database: VocabularyDatabase): VocabularyDao {
        return database.vocabularyDao()
    }
}