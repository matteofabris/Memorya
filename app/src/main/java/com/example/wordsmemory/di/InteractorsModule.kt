package com.example.wordsmemory.di

import android.content.Context
import androidx.work.WorkManager
import com.example.wordsmemory.data.manager.AuthenticationManager
import com.example.wordsmemory.data.manager.UserManager
import com.example.wordsmemory.data.manager.VocabularyManager
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
        vocabularyManager: VocabularyManager,
        userManager: UserManager,
        authenticationManager: AuthenticationManager
    ): Interactors {
        return Interactors(
            FetchCloudDb(vocabularyManager),
            AddUser(userManager),
            RemoveAllUsers(userManager),
            GetVocabularyItems(vocabularyManager),
            GetCategoriesAsLiveData(vocabularyManager),
            GetCategories(vocabularyManager),
            GetAccessToken(authenticationManager),
            AddCategory(vocabularyManager)
        )
    }

    @Provides
    @Singleton
    fun provideVocabularyManager(
        workManager: WorkManager,
        vocabularyItemDao: VocabularyItemDao,
        categoryDao: CategoryDao
    ): VocabularyManager {
        return VocabularyManager(
            CloudDbServiceImpl(workManager),
            VocabularyItemDataSourceImpl(vocabularyItemDao),
            CategoryDataSourceImpl(categoryDao, workManager)
        )
    }

    @Provides
    @Singleton
    fun provideUserManager(userDao: UserDao, workManager: WorkManager): UserManager {
        return UserManager(UserDataSourceImpl(userDao, workManager))
    }

    @Provides
    @Singleton
    fun provideAuthenticationManager(): AuthenticationManager {
        return AuthenticationManager(RESTServiceImpl())
    }

    @Provides
    fun provideWorkManager(@ApplicationContext appContext: Context): WorkManager {
        return WorkManager.getInstance(appContext)
    }
}