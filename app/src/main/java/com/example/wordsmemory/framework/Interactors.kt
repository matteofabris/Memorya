package com.example.wordsmemory.framework

import com.example.wordsmemory.interactors.AddUser
import com.example.wordsmemory.interactors.FetchCloudDb
import com.example.wordsmemory.interactors.RemoveAllUsers

data class Interactors(
    val fetchCloudDb: FetchCloudDb,
    val addUser: AddUser,
    val removeAllUsers: RemoveAllUsers
)