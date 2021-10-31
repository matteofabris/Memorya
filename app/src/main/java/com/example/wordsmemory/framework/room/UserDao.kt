package com.example.wordsmemory.framework.room

import androidx.room.*
import com.example.wordsmemory.model.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(userEntity: UserEntity)

    @Update
    suspend fun updateUser(userEntity: UserEntity)

    @Delete
    suspend fun deleteUser(userEntity: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM user")
    suspend fun getUsers(): List<UserEntity>
}