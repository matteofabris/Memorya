package com.example.wordsmemory.model.vocabulary

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
    override val id: Int,
    @ColumnInfo(name = "en_word")
    var enWord: String,
    @ColumnInfo(name = "it_word")
    var itWord: String,
    @ColumnInfo(name = "category", index = true)
    var category: Int
) : IItem {
    constructor (enWord: String, itWord: String) : this(0, enWord, itWord, 1)
    constructor (enWord: String, itWord: String, category: Int) : this(0, enWord, itWord, category)
    constructor () : this(0, "", "", 1)
}