package com.example.wordsmemory.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vocabulary_item", foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("category"),
        onDelete = ForeignKey.CASCADE
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
