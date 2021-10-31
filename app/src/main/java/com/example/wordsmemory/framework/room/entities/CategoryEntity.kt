package com.example.wordsmemory.framework.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wordsmemory.domain.IItem

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    @ColumnInfo(name = "category")
    var category: String
) : IItem {
    constructor (category: String) : this(0, category)
    constructor () : this(0, "")
}
