package com.example.wordsmemory

import android.content.Context
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