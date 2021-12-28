package com.memorya.framework.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.memorya.domain.Category

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
