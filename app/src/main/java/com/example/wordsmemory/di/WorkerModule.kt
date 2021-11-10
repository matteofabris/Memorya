package com.example.wordsmemory.di

import android.content.Context
import androidx.work.WorkManager
import com.example.wordsmemory.framework.worker.CloudDbSyncWorkerManager
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.dao.UserDao
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
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