package com.memorya.data.interfaces

import com.memorya.domain.Constants

interface CloudDbService {
    fun fetchCloudDb()
    fun add(type: Constants.CloudDbObjectType, id: Int)
    fun remove(type: Constants.CloudDbObjectType, id: Int)
}