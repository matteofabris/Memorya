package com.example.wordsmemory.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wordsmemory.Constants
import com.example.wordsmemory.model.Category
import com.example.wordsmemory.model.VocabularyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@Database(entities = [VocabularyItem::class, Category::class], version = 1, exportSchema = false)
abstract class VocabularyDatabase : RoomDatabase() {

    abstract fun vocabularyDao(): VocabularyDao

    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        @InternalCoroutinesApi
        fun getInstance(context: Context): VocabularyDatabase {
            kotlinx.coroutines.internal.synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        VocabularyDatabase::class.java, "vocabulary_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance

                    insertDefaultCategory()
                }

                return instance
            }
        }

        private fun insertDefaultCategory() {
            val dao = INSTANCE!!.vocabularyDao()
            GlobalScope.launch(Dispatchers.IO) {
                val categories = dao.getCategories()
                if (categories.isEmpty())
                    dao.insertCategory(
                        Category(
                            0,
                            Constants.defaultCategory
                        )
                    )
            }
        }
    }
}