package com.example.wordsmemory.di

import android.content.Context
import androidx.work.WorkManager
import com.example.wordsmemory.data.repository.UserRepository
import com.example.wordsmemory.data.repository.VocabularyRepository
import com.example.wordsmemory.framework.CloudDbServiceImpl
import com.example.wordsmemory.framework.Interactors
import com.example.wordsmemory.framework.UserDataSourceImpl
import com.example.wordsmemory.framework.room.UserDao
import com.example.wordsmemory.interactors.AddUser
import com.example.wordsmemory.interactors.FetchCloudDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object InteractorsModule {

    @Provides
    @Singleton
    fun provideInteractors(
        vocabularyRepository: VocabularyRepository,
        userRepository: UserRepository
    ): Interactors {
        return Interactors(FetchCloudDb(vocabularyRepository), AddUser(userRepository))
    }

    @Provides
    @Singleton
    fun provideVocabularyRepository(workManager: WorkManager): VocabularyRepository {
        return VocabularyRepository(CloudDbServiceImpl(workManager))
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, workManager: WorkManager): UserRepository {
        return UserRepository(UserDataSourceImpl(userDao, workManager))
    }

    @Provides
    fun provideWorkManager(@ApplicationContext appContext: Context): WorkManager {
        return WorkManager.getInstance(appContext)
    }
}