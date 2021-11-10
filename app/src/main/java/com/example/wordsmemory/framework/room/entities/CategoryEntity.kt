package com.example.wordsmemory.framework.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wordsmemory.domain.Category
import com.example.wordsmemory.domain.IItem

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    @ColumnInfo(name = "category")
    override var category: String
) : Category {
    constructor (category: String) : this(0, category)
    constructor () : this(0, "")
    constructor(category: Category) : this(category.id, category.category)
}
