package com.example.wordsmemory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Entity(tableName = "en_vocabulary")
data class EnVocabulary(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "en_word")
    val enWord: String,
    @ColumnInfo(name = "it_word")
    val itWord: String
) {
    constructor (enWord: String, itWord: String) : this(0, enWord, itWord)
}

@Dao
interface EnVocabularyDao {
    @Insert
    suspend fun insert(word: EnVocabulary)

    @Update
    suspend fun update(vararg words: EnVocabulary)

    @Delete
    suspend fun delete(word: EnVocabulary)

    @Query("SELECT * FROM en_vocabulary")
    fun getAll(): LiveData<List<EnVocabulary>>
}

@Database(entities = [EnVocabulary::class], version = 1, exportSchema = false)
abstract class VocabularyDatabase : RoomDatabase() {
    abstract fun enVocabularyDao(): EnVocabularyDao

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
                }
                return instance
            }
        }
    }
}