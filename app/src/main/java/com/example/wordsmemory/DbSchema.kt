package com.example.wordsmemory

import androidx.room.*

@Entity(tableName = "en_vocabulary")
data class EnVocabulary(
    @PrimaryKey val id: Int,

    val enWord: String,
    val itWord: String
)

@Dao
interface EnVocabularyDao {
    @Insert
    fun insertAll(vararg words: EnVocabulary)

    @Insert
    fun insert(word: EnVocabulary)

    @Update
    fun update(vararg words: EnVocabulary)

    @Delete
    fun delete(word: EnVocabulary)

    @Query("SELECT * FROM en_vocabulary")
    fun getAll(): List<EnVocabulary>
}

@Database(entities = [EnVocabulary::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): EnVocabularyDao
}