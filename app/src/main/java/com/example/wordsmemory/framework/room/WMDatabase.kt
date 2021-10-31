package com.example.wordsmemory.framework.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wordsmemory.Constants
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.dao.UserDao
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import com.example.wordsmemory.framework.room.entities.UserEntity
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@Database(
    entities = [VocabularyItemEntity::class, CategoryEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WMDatabase : RoomDatabase() {

    abstract fun vocabularyItemDao(): VocabularyItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: WMDatabase? = null

        @InternalCoroutinesApi
        fun getInstance(context: Context): WMDatabase {
            kotlinx.coroutines.internal.synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WMDatabase::class.java, "wm_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance

                    insertDefaultCategory()
                }

                return instance
            }
        }

        private fun insertDefaultCategory() {
            val dao = INSTANCE!!.categoryDao()
            GlobalScope.launch(Dispatchers.IO) {
                val categories = dao.getCategories().value
                if (categories?.isEmpty() == true)
                    dao.insertCategory(
                        CategoryEntity(
                            0,
                            Constants.defaultCategory
                        )
                    )
            }
        }
    }
}