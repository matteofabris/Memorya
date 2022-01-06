package com.memorya.data.interfaces

import com.memorya.domain.Result

interface RESTService {
    suspend fun translate(text: String): Result<String>
}