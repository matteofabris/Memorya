package com.example.wordsmemory.framework.worker

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

class CloudDbSyncWorkerManager(
    private val _vocabularyItemDao: VocabularyItemDao,
    private val _categoryDao: CategoryDao,
    private val _userDao: UserDao,
    private val _firestoreDb: FirebaseFirestore
) {
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun fetchCloudDb(): ListenableWorker.Result {
        val localUser = _userDao.getUsers().first()
        val cloudUserRef = _firestoreDb.collection(Constants.users).document(localUser.userId)
        var result = ListenableWorker.Result.success()

        try {
            val cloudUserdoc = Tasks.await(cloudUserRef.get())

            if (cloudUserdoc.exists()) {
                Log.d(Constants.packageName, "FIRESTORE: user is in cloud")

                updateLocalDbVocabularyItems(cloudUserRef)
                updateLocalDbCategories(cloudUserRef)
            }
        } catch (e: ExecutionException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun updateLocalDbVocabularyItems(cloudUserRef: DocumentReference) {
        val cloudVocabularyItems = mutableListOf<IItem>()
        val localVocabularyItems = _vocabularyItemDao.getVocabularyItems()

        val cloudVocabularyItemsCollection =
            Tasks.await(cloudUserRef.collection(Constants.vocabularyItems).get())

        if (cloudVocabularyItemsCollection.isEmpty) return

        cloudVocabularyItemsCollection.forEach { item ->
            cloudVocabularyItems.add(item.toObject<VocabularyItemEntity>())
        }

        deleteObsoleteLocalItems(
            cloudVocabularyItems,
            localVocabularyItems
        )
        insertOrUpdateLocalItems(
            cloudVocabularyItems,
            localVocabularyItems
        )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun updateLocalDbCategories(
        cloudUserRef: DocumentReference
    ) {
        val cloudCategories = mutableListOf<IItem>()
        val localCategories = _categoryDao.getCategories()

        val cloudCategoriesCollection =
            Tasks.await(cloudUserRef.collection(Constants.categories).get())

        if (cloudCategoriesCollection.isEmpty) return

        cloudCategoriesCollection.forEach { item ->
            cloudCategories.add(item.toObject<CategoryEntity>())
        }

        deleteObsoleteLocalItems(cloudCategories, localCategories)
        insertOrUpdateLocalItems(cloudCategories, localCategories)
    }

    private suspend fun deleteObsoleteLocalItems(
        cloudItems: MutableList<IItem>,
        localItems: List<IItem>
    ) {
        localItems.forEach { localItem ->
            if (cloudItems.firstOrNull { it.id == localItem.id } == null) {
                when (localItem) {
                    is VocabularyItemEntity -> _vocabularyItemDao.deleteVocabularyItem(localItem)
                    is CategoryEntity -> {
                        if (localItem.category != Constants.defaultCategory)
                            _categoryDao.deleteCategory(localItem)
                    }
                }
            }
        }
    }

    private suspend fun insertOrUpdateLocalItems(
        cloudItems: MutableList<IItem>,
        localItems: List<IItem>
    ) {
        cloudItems.forEach { cloudItem ->
            if (localItems.firstOrNull { it.id == cloudItem.id } != null) {
                when (cloudItem) {
                    is VocabularyItemEntity -> _vocabularyItemDao.updateVocabularyItem(cloudItem)
                    is CategoryEntity -> _categoryDao.updateCategory(cloudItem)
                }
            } else {
                when (cloudItem) {
                    is VocabularyItemEntity -> _vocabularyItemDao.insertVocabularyItem(cloudItem)
                    is CategoryEntity -> _categoryDao.insertCategory(cloudItem)
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun insertCloudDbUser(): ListenableWorker.Result {
        val localUserId = _userDao.getUsers().first().userId
        var result = ListenableWorker.Result.success()

        try {
            val cloudUserDoc =
                Tasks.await(_firestoreDb.collection(Constants.users).document(localUserId).get())
            if (cloudUserDoc.exists()) return result

            setCloudDbUserDoc(localUserId)
        } catch (e: ExecutionException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    private fun setCloudDbUserDoc(localUserId: String) {
        Log.d(Constants.packageName, "FIRESTORE: add user")

        val task = _firestoreDb.collection(Constants.users).document(localUserId).set(
            hashMapOf(
                "id" to localUserId
            )
        )
        Tasks.await(task)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun updateCloudDbVocabularyItem(localVocabularyItemId: Int): ListenableWorker.Result {
        val cloudVocabularyItemRef =
            _firestoreDb.collection(Constants.users).document(_userDao.getUsers().first().userId)
                .collection(Constants.vocabularyItems)
                .document(localVocabularyItemId.toString())
        val localVocabularyItem = _vocabularyItemDao.getVocabularyItemById(localVocabularyItemId)
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
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun updateCloudDbCategory(localCategoryId: Int): ListenableWorker.Result {
        val cloudCategoryRef =
            _firestoreDb.collection(Constants.users).document(_userDao.getUsers().first().userId)
                .collection(Constants.categories).document(localCategoryId.toString())
        val localCategory = _categoryDao.getCategoryById(localCategoryId)
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
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun deleteVocabularyItem(localVocabularyItemId: Int): ListenableWorker.Result {
        var result = ListenableWorker.Result.success()

        try {
            Tasks.await(
                _firestoreDb.collection(Constants.users)
                    .document(_userDao.getUsers().first().userId)
                    .collection(Constants.vocabularyItems)
                    .document(localVocabularyItemId.toString())
                    .delete()
            )
        } catch (e: ExecutionException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun deleteCategory(localCategoryId: Int): ListenableWorker.Result {
        var result = ListenableWorker.Result.success()

        try {
            Tasks.await(
                _firestoreDb.collection(Constants.users)
                    .document(_userDao.getUsers().first().userId)
                    .collection(Constants.categories).document(localCategoryId.toString()).delete()
            )
        } catch (e: ExecutionException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        } catch (e: InterruptedException) {
            Log.e(Constants.packageName, e.toString())
            result = ListenableWorker.Result.retry()
        }

        return result
    }
}