package com.example.wordsmemory.framework

import android.util.Log
import androidx.work.ListenableWorker
import com.example.wordsmemory.Constants
import com.example.wordsmemory.domain.IItem
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.dao.UserDao
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.concurrent.ExecutionException

object CloudDbSyncHelper {
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun fetchCloudDb(
        userDao: UserDao,
        vocabularyItemDao: VocabularyItemDao,
        categoryDao: CategoryDao,
        firestoreDb: FirebaseFirestore
    ): ListenableWorker.Result {
        val localUser = userDao.getUsers().first()
        val cloudUserRef = firestoreDb.collection(Constants.users).document(localUser.userId)
        var result = ListenableWorker.Result.success()

        try {
            val cloudUserdoc = Tasks.await(cloudUserRef.get())

            if (cloudUserdoc.exists()) {
                Log.d("FIRESTORE", "FIRESTORE: user is in cloud")

                updateLocalDbVocabularyItems(cloudUserRef, vocabularyItemDao, categoryDao)
                updateLocalDbCategories(cloudUserRef, vocabularyItemDao, categoryDao)
            }
        } catch (e: ExecutionException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun updateLocalDbVocabularyItems(
        cloudUserRef: DocumentReference,
        vocabularyItemDao: VocabularyItemDao,
        categoryDao: CategoryDao
    ) {
        val cloudVocabularyItems = mutableListOf<IItem>()
        val localVocabularyItems = vocabularyItemDao.getVocabularyItems().value!!

        val cloudVocabularyItemsCollection =
            Tasks.await(cloudUserRef.collection(Constants.vocabularyItems).get())

        if (cloudVocabularyItemsCollection.isEmpty) return

        cloudVocabularyItemsCollection.forEach { item ->
            cloudVocabularyItems.add(item.toObject<VocabularyItemEntity>())
        }

        deleteObsoleteLocalItems(
            cloudVocabularyItems,
            localVocabularyItems,
            vocabularyItemDao,
            categoryDao
        )
        insertOrUpdateLocalItems(
            cloudVocabularyItems,
            localVocabularyItems,
            vocabularyItemDao,
            categoryDao
        )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun updateLocalDbCategories(
        cloudUserRef: DocumentReference,
        vocabularyItemDao: VocabularyItemDao,
        categoryDao: CategoryDao
    ) {
        val cloudCategories = mutableListOf<IItem>()
        val localCategories = categoryDao.getCategories().value!!

        val cloudCategoriesCollection =
            Tasks.await(cloudUserRef.collection(Constants.categories).get())

        if (cloudCategoriesCollection.isEmpty) return

        cloudCategoriesCollection.forEach { item ->
            cloudCategories.add(item.toObject<CategoryEntity>())
        }

        deleteObsoleteLocalItems(cloudCategories, localCategories, vocabularyItemDao, categoryDao)
        insertOrUpdateLocalItems(cloudCategories, localCategories, vocabularyItemDao, categoryDao)
    }

    private suspend fun deleteObsoleteLocalItems(
        cloudItems: MutableList<IItem>,
        localItems: List<IItem>,
        vocabularyItemDao: VocabularyItemDao,
        categoryDao: CategoryDao
    ) {
        localItems.forEach { localItem ->
            if (cloudItems.firstOrNull { it.id == localItem.id } == null) {
                when (localItem) {
                    is VocabularyItemEntity -> vocabularyItemDao.deleteVocabularyItem(localItem)
                    is CategoryEntity -> {
                        if (localItem.category != Constants.defaultCategory)
                            categoryDao.deleteCategory(localItem)
                    }
                }
            }
        }
    }

    private suspend fun insertOrUpdateLocalItems(
        cloudItems: MutableList<IItem>,
        localItems: List<IItem>,
        vocabularyItemDao: VocabularyItemDao,
        categoryDao: CategoryDao
    ) {
        cloudItems.forEach { cloudItem ->
            if (localItems.firstOrNull { it.id == cloudItem.id } != null) {
                when (cloudItem) {
                    is VocabularyItemEntity -> vocabularyItemDao.updateVocabularyItem(cloudItem)
                    is CategoryEntity -> categoryDao.updateCategory(cloudItem)
                }
            } else {
                when (cloudItem) {
                    is VocabularyItemEntity -> vocabularyItemDao.insertVocabularyItem(cloudItem)
                    is CategoryEntity -> categoryDao.insertCategory(cloudItem)
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun insertCloudDbUser(
        userDao: UserDao,
        firestoreDb: FirebaseFirestore
    ): ListenableWorker.Result {
        val localUserId = userDao.getUsers().first().userId
        var result = ListenableWorker.Result.success()

        try {
            val cloudUserDoc =
                Tasks.await(firestoreDb.collection(Constants.users).document(localUserId).get())
            if (cloudUserDoc.exists()) return result

            setCloudDbUserDoc(firestoreDb, localUserId)
        } catch (e: ExecutionException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    private fun setCloudDbUserDoc(
        firestoreDb: FirebaseFirestore,
        localUserId: String
    ) {
        Log.d("FIRESTORE", "FIRESTORE: add user")

        val task = firestoreDb.collection(Constants.users).document(localUserId).set(
            hashMapOf(
                "id" to localUserId
            )
        )
        Tasks.await(task)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun updateCloudDbVocabularyItem(
        userDao: UserDao,
        vocabularyItemDao: VocabularyItemDao,
        firestoreDb: FirebaseFirestore,
        localVocabularyItemId: Int
    ): ListenableWorker.Result {
        val cloudVocabularyItemRef =
            firestoreDb.collection(Constants.users).document(userDao.getUsers().first().userId)
                .collection(Constants.vocabularyItems)
                .document(localVocabularyItemId.toString())
        val localVocabularyItem = vocabularyItemDao.getVocabularyItemById(localVocabularyItemId)
        var result = ListenableWorker.Result.success()

        try {
            val cloudVocabularyItemDoc = Tasks.await(cloudVocabularyItemRef.get())
            if (cloudVocabularyItemDoc.exists()) {
                val cloudVocabularyItem = cloudVocabularyItemDoc.toObject<VocabularyItemEntity>()

                if (cloudVocabularyItem != null) {
                    if (cloudVocabularyItem.enWord != localVocabularyItem.enWord)
                        Tasks.await(
                            cloudVocabularyItemRef.update(
                                "enWord",
                                localVocabularyItem.enWord
                            )
                        )

                    if (cloudVocabularyItem.itWord != localVocabularyItem.itWord)
                        Tasks.await(
                            cloudVocabularyItemRef.update(
                                "itWord",
                                localVocabularyItem.itWord
                            )
                        )

                    if (cloudVocabularyItem.category != localVocabularyItem.category)
                        Tasks.await(
                            cloudVocabularyItemRef.update(
                                "category",
                                localVocabularyItem.category
                            )
                        )
                }
            } else {
                Tasks.await(cloudVocabularyItemRef.set(localVocabularyItem))
            }
        } catch (e: ExecutionException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun updateCloudDbCategory(
        userDao: UserDao,
        categoryDao: CategoryDao,
        firestoreDb: FirebaseFirestore,
        localCategoryId: Int
    ): ListenableWorker.Result {
        val cloudCategoryRef =
            firestoreDb.collection(Constants.users).document(userDao.getUsers().first().userId)
                .collection(Constants.categories).document(localCategoryId.toString())
        val localCategory = categoryDao.getCategoryById(localCategoryId)
        var result = ListenableWorker.Result.success()

        try {
            val cloudCategoryDoc = Tasks.await(cloudCategoryRef.get())
            if (cloudCategoryDoc.exists()) {
                val cloudCategory = cloudCategoryDoc.toObject<CategoryEntity>()

                if (cloudCategory != null) {
                    if (cloudCategory.category != localCategory.category)
                        Tasks.await(cloudCategoryRef.update("category", localCategory.category))
                }
            } else {
                Tasks.await(cloudCategoryRef.set(localCategory))
            }
        } catch (e: ExecutionException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun deleteVocabularyItem(
        userDao: UserDao,
        firestoreDb: FirebaseFirestore,
        localVocabularyItemId: Int
    ): ListenableWorker.Result {
        var result = ListenableWorker.Result.success()

        try {
            Tasks.await(
                firestoreDb.collection(Constants.users).document(userDao.getUsers().first().userId)
                    .collection(Constants.vocabularyItems)
                    .document(localVocabularyItemId.toString())
                    .delete()
            )
        } catch (e: ExecutionException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun deleteCategory(
        userDao: UserDao,
        firestoreDb: FirebaseFirestore,
        localCategoryId: Int
    ): ListenableWorker.Result {
        var result = ListenableWorker.Result.success()

        try {
            Tasks.await(
                firestoreDb.collection(Constants.users).document(userDao.getUsers().first().userId)
                    .collection(Constants.categories).document(localCategoryId.toString()).delete()
            )
        } catch (e: ExecutionException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e("ERROR", e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }
}