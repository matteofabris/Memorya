package com.example.wordsmemory.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "user_id")
    var userId: String,
    @ColumnInfo(name = "access_token")
    var accessToken: String = ""
) {
    constructor (userId: String, accessToken: String) : this(0, userId, accessToken)
}