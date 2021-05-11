package com.example.wordsmemory.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "category")
    var category: String
) {
    constructor (category: String) : this(0, category)
}
