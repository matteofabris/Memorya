package com.example.wordsmemory.di

import android.content.Context
import androidx.work.WorkManager
import com.example.wordsmemory.data.repository.UserRepository
import com.example.wordsmemory.data.repository.VocabularyRepository
import com.example.wordsmemory.framework.*
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.dao.UserDao
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
import com.example.wordsmemory.interactors.*
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
        return Interactors(
            FetchCloudDb(vocabularyRepository),
            AddUser(userRepository),
            RemoveAllUsers(userRepository),
            GetVocabularyItems(vocabularyRepository),
            GetCategories(vocabularyRepository)
        )
    }

    @Provides
    @Singleton
    fun provideVocabularyRepository(
        workManager: WorkManager,
        vocabularyItemDao: VocabularyItemDao,
        categoryDao: CategoryDao
    ): VocabularyRepository {
        return VocabularyRepository(
            CloudDbServiceImpl(workManager),
            VocabularyItemDataSourceImpl(vocabularyItemDao),
            CategoryDataSourceImpl(categoryDao)
        )
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