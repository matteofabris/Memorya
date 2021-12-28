package com.memorya.di

import androidx.work.WorkManager
import com.memorya.data.manager.UserManager
import com.memorya.data.manager.VocabularyManager
import com.memorya.framework.Interactors
import com.memorya.framework.implementations.*
import com.memorya.framework.room.dao.CategoryDao
import com.memorya.framework.room.dao.UserDao
import com.memorya.framework.room.dao.VocabularyItemDao
import com.memorya.interactors.*
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
        GetNotEmptyCategoriesAsLiveData(vocabularyManager),
        GetCategories(vocabularyManager),
        GetAuthTokens(userManager),
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
        VocabularyItemDataSourceImpl(vocabularyItemDao),
        CategoryDataSourceImpl(categoryDao),
        RESTServiceImpl(userDao),
        UserDataSourceImpl(userDao)
    )

    @Provides
    @Singleton
    fun provideUserManager(userDao: UserDao, workManager: WorkManager) =
        UserManager(
            UserDataSourceImpl(userDao),
            RESTServiceImpl(userDao),
            CloudDbServiceImpl(workManager)
        )
}