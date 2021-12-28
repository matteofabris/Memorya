package com.memorya.framework.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.memorya.domain.VocabularyItem

@Entity(
    tableName = "vocabulary_item", foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("category"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class VocabularyItemEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    @ColumnInfo(name = "en_word")
    override var enWord: String,
    @ColumnInfo(name = "it_word")
    override var itWord: String,
    @ColumnInfo(name = "category", index = true)
    override var category: Int
) : VocabularyItem {
    constructor (enWord: String, itWord: String) : this(0, enWord, itWord, 1)
    constructor (enWord: String, itWord: String, category: Int) : this(0, enWord, itWord, category)
    constructor () : this(0, "", "", 1)
    constructor (vocabularyItem: VocabularyItem) : this(
        vocabularyItem.id,
        vocabularyItem.enWord,
        vocabularyItem.itWord,
        vocabularyItem.category
    )
}