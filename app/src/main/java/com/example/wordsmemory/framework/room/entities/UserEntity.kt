package com.example.wordsmemory.framework.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wordsmemory.domain.User

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    @ColumnInfo(name = "user_id")
    override var userId: String,
    @ColumnInfo(name = "access_token")
    override var accessToken: String = "",
    @ColumnInfo(name = "refresh_token")
    override var refreshToken: String = ""
) : User {
    constructor (userId: String, accessToken: String, refreshToken: String) : this(
        0,
        userId,
        accessToken,
        refreshToken
    )

    constructor (user: User) : this(user.id, user.userId, user.accessToken, user.refreshToken)
}