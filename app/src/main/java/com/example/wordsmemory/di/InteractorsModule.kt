package com.example.wordsmemory.di

import androidx.work.WorkManager
import com.example.wordsmemory.data.manager.UserManager
import com.example.wordsmemory.data.manager.VocabularyManager
import com.example.wordsmemory.framework.Interactors
import com.example.wordsmemory.framework.implementations.*
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.dao.UserDao
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
import com.example.wordsmemory.interactors.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object InteractorsModule {

    @Provides
    @Singleton
    fun provideInteractors(
        vocabularyManager: VocabularyManager,
        userManager: UserManager
    ) = Interactors(
        FetchCloudDb(vocabularyManager),
        AddUser(userManager),
        RemoveAllUsers(userManager),
        GetVocabularyItems(vocabularyManager),
        GetVocabularyItemsAsLiveData(vocabularyManager),
        GetCategoriesAsLiveData(vocabularyManager),
        GetCategories(vocabularyManager),
        GetAccessToken(userManager),
        AddCategory(vocabularyManager),
        AddVocabularyItem(vocabularyManager),
        Translate(vocabularyManager),
        RemoveCategory(vocabularyManager),
        RemoveVocabularyItem(vocabularyManager)
    )

    @Provides
    @Singleton
    fun provideVocabularyManager(
        workManager: WorkManager,
        vocabularyItemDao: VocabularyItemDao,
        categoryDao: CategoryDao,
        userDao: UserDao
    ) = VocabularyManager(
        CloudDbServiceImpl(workManager),
        VocabularyItemDataSourceImpl(vocabularyItemDao, workManager),
        CategoryDataSourceImpl(categoryDao, workManager),
        RESTServiceImpl(userDao)
    )

    @Provides
    @Singleton
    fun provideUserManager(userDao: UserDao, workManager: WorkManager) =
        UserManager(UserDataSourceImpl(userDao, workManager), RESTServiceImpl(userDao))
}