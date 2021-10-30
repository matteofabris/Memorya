package com.example.wordsmemory.framework.db

import androidx.room.*
import com.example.wordsmemory.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM user")
    suspend fun getUsers(): List<User>
}