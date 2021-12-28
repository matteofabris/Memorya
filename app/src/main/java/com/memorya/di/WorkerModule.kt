package com.memorya.di

import android.content.Context
import androidx.work.WorkManager
import com.memorya.framework.worker.CloudDbSyncWorkerManager
import com.memorya.framework.room.dao.CategoryDao
import com.memorya.framework.room.dao.UserDao
import com.memorya.framework.room.dao.VocabularyItemDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WorkerModule {

    @Provides
    fun provideWorkManager(@ApplicationContext appContext: Context) =
        WorkManager.getInstance(appContext)

    @Provides
    @Singleton
    fun provideCloudDbSyncWorkerManager(vocabularyItemDao: VocabularyItemDao,
                                        categoryDao: CategoryDao,
                                        userDao: UserDao,
                                        firestoreDb: FirebaseFirestore
    ) = CloudDbSyncWorkerManager(vocabularyItemDao, categoryDao, userDao, firestoreDb)
}