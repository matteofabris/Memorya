package com.memorya.framework.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.memorya.domain.User

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    @ColumnInfo(name = "user_id")
    override var userId: String
) : User {
    constructor (userId: String) : this(0, userId)

    constructor (user: User) : this(user.id, user.userId)
}