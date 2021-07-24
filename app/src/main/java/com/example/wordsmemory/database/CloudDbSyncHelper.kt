package com.example.wordsmemory.database

import android.util.Log
import com.example.wordsmemory.Constants
import com.example.wordsmemory.model.vocabulary.Category
import com.example.wordsmemory.model.vocabulary.IItem
import com.example.wordsmemory.model.vocabulary.VocabularyItem
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CloudDbSyncHelper {
    fun fetchDataFromCloud(
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val localUser = dbDao.getUsers().first()
            val cloudUserRef = firestoreDb.collection(Constants.users).document(localUser.userId)

            cloudUserRef.get().addOnSuccessListener { cloudUserDoc ->
                if (cloudUserDoc.exists()) {
                    Log.d("FIRESTORE", "FIRESTORE: user is in cloud")

                    CoroutineScope(Dispatchers.IO).launch {
                        updateLocalDbVocabularyItems(cloudUserRef, dbDao)
                        updateLocalDbCategories(cloudUserRef, dbDao)
                    }
                }
            }
        }
    }

    private suspend fun updateLocalDbVocabularyItems(
        cloudUserRef: DocumentReference,
        dbDao: WMDao
    ) {
        val cloudVocabularyItems = mutableListOf<IItem>()
        val localVocabularyItems = dbDao.getVocabularyItems()

        cloudUserRef.collection(Constants.vocabularyItems).get()
            .addOnSuccessListener { cloudVocabularyItemsCollection ->
                if (cloudVocabularyItemsCollection.isEmpty) return@addOnSuccessListener

                cloudVocabularyItemsCollection.forEach { item ->
                    cloudVocabularyItems.add(item.toObject<VocabularyItem>())
                }

                CoroutineScope(Dispatchers.IO).launch {
                    deleteObsoleteLocalItems(cloudVocabularyItems, localVocabularyItems, dbDao)
                    insertOrUpdateLocalItems(cloudVocabularyItems, localVocabularyItems, dbDao)
                }
            }
    }

    private suspend fun updateLocalDbCategories(
        cloudUserRef: DocumentReference,
        dbDao: WMDao
    ) {
        val cloudCategories = mutableListOf<IItem>()
        val localCategories = dbDao.getCategories()

        cloudUserRef.collection(Constants.categories).get()
            .addOnSuccessListener { cloudCategoriesCollection ->
                if (cloudCategoriesCollection.isEmpty) return@addOnSuccessListener

                cloudCategoriesCollection.forEach { item ->
                    cloudCategories.add(item.toObject<Category>())
                }

                CoroutineScope(Dispatchers.IO).launch {
                    deleteObsoleteLocalItems(cloudCategories, localCategories, dbDao)
                    insertOrUpdateLocalItems(cloudCategories, localCategories, dbDao)
                }
            }
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

    fun insertCloudDbUser(
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val localUserId = dbDao.getUsers().first().userId

            firestoreDb.collection(Constants.users).document(localUserId).get()
                .addOnSuccessListener { cloudUserDoc ->
                    if (cloudUserDoc.exists()) return@addOnSuccessListener

                    Log.d("FIRESTORE", "FIRESTORE: add user")

                    firestoreDb.collection(Constants.users).document(localUserId).set(
                        hashMapOf(
                            "id" to localUserId
                        )
                    )
                }
        }
    }

    fun updateCloudDbVocabularyItem(
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore,
        localVocabularyItemId: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val cloudVocabularyItemRef =
                firestoreDb.collection(Constants.users).document(dbDao.getUsers().first().userId)
                    .collection(Constants.vocabularyItems)
                    .document(localVocabularyItemId.toString())
            val localVocabularyItem = dbDao.getVocabularyItemById(localVocabularyItemId)

            cloudVocabularyItemRef.get().addOnSuccessListener { cloudVocabularyItemDoc ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (cloudVocabularyItemDoc.exists()) {
                        val cloudVocabularyItem = cloudVocabularyItemDoc.toObject<VocabularyItem>()

                        if (cloudVocabularyItem != null) {
                            if (cloudVocabularyItem.enWord != localVocabularyItem.enWord)
                                cloudVocabularyItemRef.update("enWord", localVocabularyItem.enWord)

                            if (cloudVocabularyItem.itWord != localVocabularyItem.itWord)
                                cloudVocabularyItemRef.update("itWord", localVocabularyItem.itWord)

                            if (cloudVocabularyItem.category != localVocabularyItem.category)
                                cloudVocabularyItemRef.update(
                                    "category",
                                    localVocabularyItem.category
                                )
                        }
                    } else {
                        cloudVocabularyItemRef.set(localVocabularyItem)
                    }
                }
            }
        }
    }

    fun updateCloudDbCategory(
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore,
        localCategoryId: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val cloudCategoryRef =
                firestoreDb.collection(Constants.users).document(dbDao.getUsers().first().userId)
                    .collection(Constants.categories).document(localCategoryId.toString())
            val localCategory = dbDao.getCategoryById(localCategoryId)

            cloudCategoryRef.get().addOnSuccessListener { cloudCategoryDoc ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (cloudCategoryDoc.exists()) {
                        val cloudCategory = cloudCategoryDoc.toObject<Category>()

                        if (cloudCategory != null) {
                            if (cloudCategory.category != localCategory.category)
                                cloudCategoryRef.update(
                                    "category",
                                    localCategory.category
                                )
                        }
                    } else {
                        cloudCategoryRef.set(localCategory)
                    }
                }
            }
        }
    }

    suspend fun deleteVocabularyItem(
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore,
        localVocabularyItemId: Int
    ) {
        firestoreDb.collection(Constants.users).document(dbDao.getUsers().first().userId)
            .collection(Constants.vocabularyItems).document(localVocabularyItemId.toString())
            .delete()
    }

    suspend fun deleteCategory(
        dbDao: WMDao,
        firestoreDb: FirebaseFirestore,
        localCategoryId: Int
    ) {
        firestoreDb.collection(Constants.users).document(dbDao.getUsers().first().userId)
            .collection(Constants.categories).document(localCategoryId.toString()).delete()
    }
}