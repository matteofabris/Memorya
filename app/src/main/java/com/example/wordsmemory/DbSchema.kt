package com.example.wordsmemory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch


@Entity(
    tableName = "vocabulary_item", foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("category"),
        onDelete = CASCADE
    )]
)
data class VocabularyItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "en_word")
    var enWord: String,
    @ColumnInfo(name = "it_word")
    var itWord: String,
    @ColumnInfo(name = "category", index = true)
    var category: Int
) {
    constructor (enWord: String, itWord: String) : this(0, enWord, itWord, 0)
    constructor (enWord: String, itWord: String, category: Int) : this(0, enWord, itWord, category)
}

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "category")
    var category: String
) {
    constructor (category: String) : this(0, category)
}

@Dao
interface VocabularyDao {
    @Insert
    suspend fun insertVocabularyItem(item: VocabularyItem)

    @Insert
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateVocabularyItem(item: VocabularyItem)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteVocabularyItem(item: VocabularyItem)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM vocabulary_item")
    fun getVocabularyItemsAsLiveData(): LiveData<List<VocabularyItem>>

    @Query("SELECT * FROM vocabulary_item WHERE id == :id")
    suspend fun getVocabularyItemById(id: Int): VocabularyItem

    @Query("SELECT * FROM vocabulary_item WHERE category == :categoryId")
    fun getVocabularyItemsByCategoryAsLiveData(categoryId: Int): LiveData<List<VocabularyItem>>

    @Query("SELECT * FROM category")
    fun getCategoriesAsLiveData(): LiveData<List<Category>>

    @Query("SELECT * FROM category")
    fun getCategories(): List<Category>

    @Query("SELECT id FROM category WHERE category == :category")
    suspend fun getCategoryId(category: String): Int

    @Query("SELECT category FROM category WHERE id == :id")
    fun getCategoryName(id: Int): String

    @Query("SELECT * FROM category WHERE id == :id")
    suspend fun getCategoryById(id: Int): Category
}

@Database(entities = [VocabularyItem::class, Category::class], version = 1, exportSchema = false)
abstract class VocabularyDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao

    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        @InternalCoroutinesApi
        fun getInstance(context: Context): VocabularyDatabase {
            synchronized(this) {
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