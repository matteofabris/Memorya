package com.example.wordsmemory.database

import android.util.Log
import androidx.work.ListenableWorker
import com.example.wordsmemory.Constants
import com.example.wordsmemory.model.vocabulary.Category
import com.example.wordsmemory.model.vocabulary.IItem
import com.example.wordsmemory.model.vocabulary.VocabularyItem
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.concurrent.ExecutionException

object CloudDbSyncHelper {
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun fetchDataFromCloud(
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore
    ): ListenableWorker.Result {
        val localUser = dbDao.getUsers().first()
        val cloudUserRef = firestoreDb.collection(Constants.users).document(localUser.userId)
        var result = ListenableWorker.Result.success()

        try {
            val cloudUserdoc = Tasks.await(cloudUserRef.get())

            if (cloudUserdoc.exists()) {
                Log.d("FIRESTORE", "FIRESTORE: user is in cloud")

                updateLocalDbVocabularyItems(cloudUserRef, dbDao)
                updateLocalDbCategories(cloudUserRef, dbDao)
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
        dbDao: WMDao
    ) {
        val cloudVocabularyItems = mutableListOf<IItem>()
        val localVocabularyItems = dbDao.getVocabularyItems()

        val cloudVocabularyItemsCollection =
            Tasks.await(cloudUserRef.collection(Constants.vocabularyItems).get())

        if (cloudVocabularyItemsCollection.isEmpty) return

        cloudVocabularyItemsCollection.forEach { item ->
            cloudVocabularyItems.add(item.toObject<VocabularyItem>())
        }

        deleteObsoleteLocalItems(cloudVocabularyItems, localVocabularyItems, dbDao)
        insertOrUpdateLocalItems(cloudVocabularyItems, localVocabularyItems, dbDao)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun updateLocalDbCategories(
        cloudUserRef: DocumentReference,
        dbDao: WMDao
    ) {
        val cloudCategories = mutableListOf<IItem>()
        val localCategories = dbDao.getCategories()

        val cloudCategoriesCollection =
            Tasks.await(cloudUserRef.collection(Constants.categories).get())

        if (cloudCategoriesCollection.isEmpty) return

        cloudCategoriesCollection.forEach { item ->
            cloudCategories.add(item.toObject<Category>())
        }

        deleteObsoleteLocalItems(cloudCategories, localCategories, dbDao)
        insertOrUpdateLocalItems(cloudCategories, localCategories, dbDao)
    }

    private suspend fun deleteObsoleteLocalItems(
        cloudItems: MutableList<IItem>,
        localItems: List<IItem>,
        dbDao: WMDao
    ) {
        localItems.forEach { localItem ->
            if (cloudItems.firstOrNull { it.id == localItem.id } == null) {
                when (localItem) {
                    is VocabularyItem -> dbDao.deleteVocabularyItem(localItem)
                    is Category -> {
                        if (localItem.category != Constants.defaultCategory)
                            dbDao.deleteCategory(localItem)
                    }
                }
            }
        }
    }

    private suspend fun insertOrUpdateLocalItems(
        cloudItems: MutableList<IItem>,
        localItems: List<IItem>,
        dbDao: WMDao
    ) {
        cloudItems.forEach { cloudItem ->
            if (localItems.firstOrNull { it.id == cloudItem.id } != null) {
                when (cloudItem) {
                    is VocabularyItem -> dbDao.updateVocabularyItem(cloudItem)
                    is Category -> dbDao.updateCategory(cloudItem)
                }
            } else {
                when (cloudItem) {
                    is VocabularyItem -> dbDao.insertVocabularyItem(cloudItem)
                    is Category -> dbDao.insertCategory(cloudItem)
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun insertCloudDbUser(
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore
    ): ListenableWorker.Result {
        val localUserId = dbDao.getUsers().first().userId
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
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore,
        localVocabularyItemId: Int
    ): ListenableWorker.Result {
        val cloudVocabularyItemRef =
            firestoreDb.collection(Constants.users).document(dbDao.getUsers().first().userId)
                .collection(Constants.vocabularyItems)
                .document(localVocabularyItemId.toString())
        val localVocabularyItem = dbDao.getVocabularyItemById(localVocabularyItemId)
        var result = ListenableWorker.Result.success()

        try {
            val cloudVocabularyItemDoc = Tasks.await(cloudVocabularyItemRef.get())
            if (cloudVocabularyItemDoc.exists()) {
                val cloudVocabularyItem = cloudVocabularyItemDoc.toObject<VocabularyItem>()

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
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore,
        localCategoryId: Int
    ): ListenableWorker.Result {
        val cloudCategoryRef =
            firestoreDb.collection(Constants.users).document(dbDao.getUsers().first().userId)
                .collection(Constants.categories).document(localCategoryId.toString())
        val localCategory = dbDao.getCategoryById(localCategoryId)
        var result = ListenableWorker.Result.success()

        try {
            val cloudCategoryDoc = Tasks.await(cloudCategoryRef.get())
            if (cloudCategoryDoc.exists()) {
                val cloudCategory = cloudCategoryDoc.toObject<Category>()

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
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore,
        localVocabularyItemId: Int
    ): ListenableWorker.Result {
        var result = ListenableWorker.Result.success()

        try {
            Tasks.await(
                firestoreDb.collection(Constants.users).document(dbDao.getUsers().first().userId)
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
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore,
        localCategoryId: Int
    ): ListenableWorker.Result {
        var result = ListenableWorker.Result.success()

        try {
            Tasks.await(
                firestoreDb.collection(Constants.users).document(dbDao.getUsers().first().userId)
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