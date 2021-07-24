package com.example.wordsmemory.model.vocabulary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    @ColumnInfo(name = "category")
    var category: String
) : IItem {
    constructor (category: String) : this(0, category)
    constructor () : this(0, "")
}
