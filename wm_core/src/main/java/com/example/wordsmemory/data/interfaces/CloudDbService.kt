package com.example.wordsmemory.data.interfaces

import com.example.wordsmemory.domain.Constants

interface CloudDbService {
    fun fetchCloudDb()
    fun add(type: Constants.CloudDbObjectType, id: Int)
    fun remove(type: Constants.CloudDbObjectType, id: Int)
}