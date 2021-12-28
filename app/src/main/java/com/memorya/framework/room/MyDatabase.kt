package com.memorya.framework.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.memorya.Constants
import com.memorya.framework.room.dao.CategoryDao
import com.memorya.framework.room.dao.UserDao
import com.memorya.framework.room.dao.VocabularyItemDao
import com.memorya.framework.room.entities.CategoryEntity
import com.memorya.framework.room.entities.UserEntity
import com.memorya.framework.room.entities.VocabularyItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@Database(
    entities = [VocabularyItemEntity::class, CategoryEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MyDatabase : RoomDatabase() {

    abstract fun vocabularyItemDao(): VocabularyItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        @InternalCoroutinesApi
        fun getInstance(context: Context): MyDatabase {
            kotlinx.coroutines.internal.synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyDatabase::class.java, "my_database"
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
                val categories = dao.getCategories()
                if (categories.isEmpty())
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